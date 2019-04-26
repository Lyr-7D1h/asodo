package com.example.ninja.Domain.stateReceivers;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v7.preference.PreferenceManager;

import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.managers.SyncManager;
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SyncStateReceiver extends BroadcastReceiver {

    private SyncManager manager;
    protected Set<SyncStateReceiver.SyncStateReceiverListener> listeners;
    protected Boolean isSynced;

    public SyncStateReceiver(SyncManager syncManager) {
        this.manager = syncManager;
        listeners = new HashSet<SyncStateReceiverListener>();
    }

    public void onReceive(Context context, Intent intent) {
        if(manager.isSynced()) {
            isSynced = true;
        } else {
            isSynced = false;
        }

        notifyStateToAll();
    }

    public void requestUpdate(SyncStateReceiverListener listener) {
        notifyState(listener);
    }

    private void notifyStateToAll() {
        for(SyncStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(SyncStateReceiverListener listener) {
        if(isSynced == null || listener == null)
            return;

        if(isSynced == true)
            listener.onSync();
        else
            listener.onDesync();
    }

    public void addListener(SyncStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(LocationStateReceiver.LocationStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface SyncStateReceiverListener {
        public void onSync();
        public void onDesync();
    }
}
