package com.example.ninja.Domain.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;

import java.util.HashSet;
import java.util.Set;

public class LocationStateReceiver extends BroadcastReceiver {

    private LocationManager manager;
    protected Set<LocationStateReceiverListener> listeners;
    protected Boolean hasLocation;

    public LocationStateReceiver(Context context) {
        manager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        listeners = new HashSet<LocationStateReceiverListener>();
        hasLocation = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void onReceive(Context context, Intent intent) {
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            hasLocation = true;
        } else {
            hasLocation = false;
        }

        notifyStateToAll();
    }

    private void notifyStateToAll() {
        for(LocationStateReceiverListener listener : listeners)
            notifyState(listener);
    }

    private void notifyState(LocationStateReceiverListener listener) {
        if(hasLocation == null || listener == null)
            return;

        if(hasLocation == true)
            listener.locationAvailable();
        else
            listener.locationUnavailable();
    }

    public void addListener(LocationStateReceiverListener l) {
        listeners.add(l);
        notifyState(l);
    }

    public void removeListener(LocationStateReceiverListener l) {
        listeners.remove(l);
    }

    public interface LocationStateReceiverListener {
        public void locationAvailable();
        public void locationUnavailable();
    }
}
