package com.example.ninja.Domain;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.LocationManager;
import android.support.v7.preference.PreferenceManager;

import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.stateReceivers.LocationStateReceiver;
import com.example.ninja.Domain.stateReceivers.NetworkStateReceiver;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Domain.util.ConnectivityUtils;
import com.example.ninja.Domain.util.LocaleUtils;
import com.example.ninja.Domain.util.UserUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.MalformedJsonException;

import java.util.Objects;

public class Global extends Application implements NetworkStateReceiver.NetworkStateReceiverListener {
    // Global variables
    private boolean languageSet;
    private boolean activeTrip;
    private int tripStatus;
    private Trip trip;
    private Intent locationIntent;

    // State receivers
    private NetworkStateReceiver networkStateReceiver;
    private LocationStateReceiver locationStateReceiver;

    // Cache
    private TripList tripCache;
    private TripList tripRegistrationQueue;
    private boolean synced;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleUtils.setLocale(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Init locale status
        setLanguageSet(false);

        // Init networkStateReceiver
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        // Init locationStateReceiver
        locationStateReceiver = new LocationStateReceiver(this);
        this.registerReceiver(locationStateReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));

        // Set trip cache
        try {
            tripCache = new TripList(CacheUtils.readCache(this, "trips.cache"));
        } catch (MalformedJsonException | NullPointerException e) {
            tripCache = new TripList();
        }

        // Set trip registration Queue
        try {
            tripRegistrationQueue = new TripList(CacheUtils.readCache(this, "tripRegistrationQueue.cache"));
        } catch (MalformedJsonException | NullPointerException e) {
            tripRegistrationQueue = new TripList();
        }

        // Sync on network
        synced = false;
        if(ConnectivityUtils.isNetworkAvailable(this)) {
            sync();
        }
    }

    public boolean isActiveTrip() {
        return activeTrip;
    }

    public void setActiveTrip(boolean activeTrip) {
        this.activeTrip = activeTrip;

        if(activeTrip) {
            updateTripStatus();
        } else {
            tripStatus = 0;
        }
    }

    public boolean isLanguageSet() {
        return languageSet;
    }

    public void setLanguageSet(boolean languageSet) {
        this.languageSet = languageSet;
    }

    public int getTripStatus() {
        return tripStatus;
    }

    public void updateTripStatus() {
        tripStatus++;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public Intent getLocationIntent() {
        return locationIntent;
    }

    public void setLocationIntent(Intent locationIntent) {
        this.locationIntent = locationIntent;
    }

    public void receiveNetworkUpdates(NetworkStateReceiver.NetworkStateReceiverListener listener) {
        networkStateReceiver.addListener(listener);
    }

    public void unregisterNetworkUpdates(NetworkStateReceiver.NetworkStateReceiverListener listener) {
        networkStateReceiver.removeListener(listener);
    }

    public LocationStateReceiver getLocationStateReceiver() {
        return locationStateReceiver;
    }

    public void setUnSynced() {
        this.synced = false;
        sync();
    }

    public void sync() {
        // Check if not already synced and has internet
        if(synced || !ConnectivityUtils.isNetworkAvailable(this)) {
            return;
        }

        // Check if user is logged in
        try {
            if(CacheUtils.readCache(this, "user.cache") == null) {
                return;
            }
        } catch (MalformedJsonException e) {
            return;
        }

        // Sync
        syncTripRegistrationQueue();
        syncTripList();
        delayedSyncTripList();

        // Update sync status
        synced = true;
    }

    private void syncTripRegistrationQueue() {
        // Register trips
        JsonArray trips = tripRegistrationQueue.getTrips();
        for (int i = 0; i < trips.size(); i++) {
            Trip trip = Trip.build(trips.get(i).getAsJsonObject());
            trip.registerToDB(this);
        }

        // Update local variable
        tripRegistrationQueue = new TripList();

        // Cache
        CacheUtils.cacheJsonObject(this, 0, tripRegistrationQueue.toJsonObject(), "tripRegistrationQueue.cache");
    }

    private void delayedSyncTripList() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                syncTripList();
            }
            catch (Exception e){
                // Do nothing
            }
        }).start();
    }

    private void syncTripList() {
        if(ConnectivityUtils.isNetworkAvailable(this)) {
            // Init
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            JsonObject json = new JsonObject();

            // Get userID
            json.add("userID", new JsonPrimitive(UserUtils.getUserID(this)));

            // Determine limit
            int limit = 10;
            if(prefs.getString("cache_size", "10") != null) {
                 limit = Integer.parseInt(Objects.requireNonNull(prefs.getString("cache_size", "10")));
            }
            json.add("limit", new JsonPrimitive(limit));

            // Make request
            Context self = this;
            AsodoRequester.newRequest("getTrips", json, this, new CustomListener() {
                @Override
                public void onResponse(JsonObject jsonResponse) {
                    // Set local variable
                    tripCache = new TripList(jsonResponse);

                    // Cache
                    CacheUtils.cacheJsonObject(self, 0, tripCache.toJsonObject(), "trips.cache");
                }
            });
        }
    }

    public void getTripCache(AsodoRequesterCallback callback) {
        if(ConnectivityUtils.isNetworkAvailable(this)) {
            JsonObject json = new JsonObject();
            json.add("userID", new JsonPrimitive(UserUtils.getUserID(this)));

            AsodoRequester.newRequest("getTrips", json, this, new CustomListener() {
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
        CacheUtils.cacheJsonObject(this, 0, tripCache.toJsonObject(), "trips.cache");
        CacheUtils.cacheJsonObject(this, 0, tripRegistrationQueue.toJsonObject(), "tripRegistrationQueue.cache");

        // Update sync status
        synced = false;
        if(ConnectivityUtils.isNetworkAvailable(this)) {
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
