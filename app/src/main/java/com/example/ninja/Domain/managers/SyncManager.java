package com.example.ninja.Domain.managers;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.stateReceivers.NetworkStateReceiver;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Domain.util.Callback;
import com.example.ninja.Domain.util.ConnectivityUtils;
import com.example.ninja.Domain.util.UserUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.MalformedJsonException;

import java.util.Objects;

public class SyncManager implements NetworkStateReceiver.NetworkStateReceiverListener {
    private Application global;
    public static String SYNC_CHANGED_ACTION = "asodo.SYNC_CHANGED";

    private TripList tripCache;
    private TripList tripRegistrationQueue;
    private boolean synced;

    public SyncManager(Application global) {
        this.global = global;

        // Init
        init();
    }

    private void init() {
        // Register to network updates
        ((Global) global).getNetworkStateReceiver().addListener(this);

        // Set trip cache
        try {
            tripCache = new TripList(CacheUtils.readCache(global, "trips.cache"));
        } catch (MalformedJsonException | NullPointerException e) {
            tripCache = new TripList();
        }

        // Set trip registration Queue
        try {
            tripRegistrationQueue = new TripList(CacheUtils.readCache(global, "tripRegistrationQueue.cache"));
        } catch (MalformedJsonException | NullPointerException e) {
            tripRegistrationQueue = new TripList();
        }

        // Sync on network
        synced = false;
        if(ConnectivityUtils.isNetworkAvailable(global)) {
            sync();
        }
    }

    public boolean isSynced() {
        return synced;
    }

    public void setUnSynced() {
        this.synced = false;
        sync();
    }

    public void sync() {
        // Check if not already synced and has internet
        if(synced || !ConnectivityUtils.isNetworkAvailable(global)) {
            return;
        }

        // Check if user is logged in
        try {
            if(CacheUtils.readCache(global, "user.cache") == null) {
                return;
            }
        } catch (MalformedJsonException e) {
            return;
        }

        // Sync registration
        syncTripRegistrationQueue(new Callback() {
            @Override
            public void callback() {
                // Sync trip list
                syncTripList(new Callback() {
                    @Override
                    public void callback() {
                        //Resync
                        delayedSyncTripList();

                        // Update sync status
                        synced = true;
                    }
                });
            }
        });
    }

    private void syncTripRegistrationQueue(Callback callback) {
        // Register trips
        registerTrip(new Callback() {
            @Override
            public void callback() {
                // Update local variable
                tripRegistrationQueue = new TripList();

                // Cache
                CacheUtils.cacheJsonObject(global, 0, tripRegistrationQueue.toJsonObject(), "tripRegistrationQueue.cache");

                // Callback
                callback.callback();
            }
        });
    }

    private void registerTrip(Callback callback) {
        JsonArray trips = tripRegistrationQueue.getTrips();
        if(trips.size() > 0) {
            // Upload next trip
            Trip trip = Trip.build(trips.get(0).getAsJsonObject());
            trip.registerToDB(global, new Callback() {
                @Override
                public void callback() {
                    // Remove from queue
                    tripRegistrationQueue.getTrips().remove(0);

                    // Register next trip
                    registerTrip(callback);
                }
            });
        } else {
            // Exit point - no trips left
            callback.callback();
        }
    }

    private void delayedSyncTripList() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                syncTripList(null);
            }
            catch (Exception e){
                // Do nothing
            }
        }).start();
    }

    private void syncTripList(Callback callback) {
        if(ConnectivityUtils.isNetworkAvailable(global)) {
            // Init
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(global);
            JsonObject json = new JsonObject();

            // Get userID
            json.add("userID", new JsonPrimitive(UserUtils.getUserID(global)));

            // Determine limit
            int limit = 10;
            if(prefs.getString("cache_size", "10") != null) {
                limit = Integer.parseInt(Objects.requireNonNull(prefs.getString("cache_size", "10")));
            }
            json.add("limit", new JsonPrimitive(limit));

            // Make request
            AsodoRequester.newRequest("getTrips", json, global, new CustomListener() {
                @Override
                public void onResponse(JsonObject jsonResponse) {
                    // Set local variable
                    tripCache = new TripList(jsonResponse);

                    // Cache
                    CacheUtils.cacheJsonObject(global, 0, tripCache.toJsonObject(), "trips.cache");

                    // Callback
                    if(callback != null) {
                        callback.callback();
                    }
                }
            });
        }
    }

    public void getTripCache(AsodoRequesterCallback callback) {
        if(ConnectivityUtils.isNetworkAvailable(global)) {
            JsonObject json = new JsonObject();
            json.add("userID", new JsonPrimitive(UserUtils.getUserID(global)));

            AsodoRequester.newRequest("getTrips", json, global, new CustomListener() {
                @Override
                public void onResponse(JsonObject jsonResponse) {
                    callback.callback(jsonResponse);
                }
            });

            return;
        }

        callback.callback(tripCache.toJsonObject());
    }

    public void addTripToCache(Trip trip) {
        // Add to lists
        tripCache.addTrip(trip);
        tripRegistrationQueue.addTrip(trip);

        // Cache
        CacheUtils.cacheJsonObject(global, 0, tripCache.toJsonObject(), "trips.cache");
        CacheUtils.cacheJsonObject(global, 0, tripRegistrationQueue.toJsonObject(), "tripRegistrationQueue.cache");

        // Update sync status
        synced = false;
        if(ConnectivityUtils.isNetworkAvailable(global)) {
            sync();
        }
    }

    @Override
    public void networkAvailable() {
        if(!synced) {
            sync();
        }
    }

    @Override
    public void networkUnavailable() {
        // Force unsync on network lost
        synced = false;
    }
}
