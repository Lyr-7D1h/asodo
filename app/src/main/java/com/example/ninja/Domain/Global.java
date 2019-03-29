package com.example.ninja.Domain;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.ninja.Domain.network.NetworkStateReceiver;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Domain.util.ConnectivityUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;

public class Global extends Application implements NetworkStateReceiver.NetworkStateReceiverListener {
    // Global variables
    private static Context sContext;
    private boolean activeTrip;
    private Trip trip;
    private Intent locationIntent;

    // Cache
    private NetworkStateReceiver networkStateReceiver;
    private TripList tripCache;
    private TripList tripRegistrationQueue;
    private boolean synced;

    @Override
    public void onCreate() {
        super.onCreate();

        // Set application context
        sContext = getApplicationContext();

        // Init networkStateReceiver
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

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

    public static Context getContext() {
        return sContext;
    }

    public boolean isActiveTrip() {
        return activeTrip;
    }

    public void setActiveTrip(boolean activeTrip) {
        this.activeTrip = activeTrip;
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

    private void sync() {
        if(synced || !ConnectivityUtils.isNetworkAvailable(this)) {
            return;
        }

        // Sync
        syncTripRegistrationQueue();
        syncTripList();

        // Cache
        CacheUtils.cacheJsonObject(this, 0, tripCache.toJsonObject(), "trips.cache");
        CacheUtils.cacheJsonObject(this, 0, tripRegistrationQueue.toJsonObject(), "tripRegistrationQueue.cache");

        // Update sync status
        synced = true;
    }

    private void syncTripRegistrationQueue() {
        System.out.println("Syncing Trip Registration Queue");

        // Register trips
        JsonArray trips = tripRegistrationQueue.getTrips();
        for (int i = 0; i < trips.size(); i++) {
            Trip trip = Trip.build(trips.get(i).getAsJsonObject());
            trip.registerToDB(this);
        }

        tripRegistrationQueue = new TripList();
    }

    private void syncTripList() {
        System.out.println("Syncing Trip List");
    }

    public TripList getTripCache() {
        return tripCache;
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
        // Do nothing
    }
}
