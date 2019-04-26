package com.example.ninja.Domain;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.drm.DrmStore;
import android.location.LocationManager;
import android.support.v7.preference.PreferenceManager;

import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.managers.ActiveTripManager;
import com.example.ninja.Domain.managers.SyncManager;
import com.example.ninja.Domain.stateReceivers.LocationStateReceiver;
import com.example.ninja.Domain.stateReceivers.NetworkStateReceiver;
import com.example.ninja.Domain.stateReceivers.SyncStateReceiver;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Domain.util.Callback;
import com.example.ninja.Domain.util.ConnectivityUtils;
import com.example.ninja.Domain.util.LocaleUtils;
import com.example.ninja.Domain.util.UserUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.MalformedJsonException;

import java.util.Objects;

public class Global extends Application {
    // Global variables
    private boolean languageSet;

    // Managers
    private ActiveTripManager activeTripManager;
    private SyncManager syncManager;

    // State receivers
    private NetworkStateReceiver networkStateReceiver;
    private LocationStateReceiver locationStateReceiver;
    private SyncStateReceiver syncStateReceiver;

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

        // Init activeTripManager
        this.activeTripManager = new ActiveTripManager();

        // Init networkStateReceiver
        networkStateReceiver = new NetworkStateReceiver();
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        // Init locationStateReceiver
        locationStateReceiver = new LocationStateReceiver(this);
        this.registerReceiver(locationStateReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));

        // Init syncManager
        this.syncManager = new SyncManager(this);

        // Init syncStateReceiver
        this.syncStateReceiver = new SyncStateReceiver(this.syncManager);
        this.registerReceiver(syncStateReceiver, new IntentFilter(SyncManager.SYNC_CHANGED_ACTION));
    }

    public ActiveTripManager getActiveTripManager() {
        return activeTripManager;
    }

    public SyncManager getSyncManager() {
        return syncManager;
    }

    public SyncStateReceiver getSyncStateReceiver() {
        return syncStateReceiver;
    }



    public boolean isLanguageSet() {
        return languageSet;
    }

    public void setLanguageSet(boolean languageSet) {
        this.languageSet = languageSet;
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

    public NetworkStateReceiver getNetworkStateReceiver() {
        return networkStateReceiver;
    }
}
