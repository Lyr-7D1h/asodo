package com.example.ninja.Domain;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.example.ninja.Domain.trips.Trip;

public class Global extends Application {
    private static Context sContext;
    private boolean activeTrip;
    private Trip trip;
    private Intent locationIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Set application context
        sContext = getApplicationContext();
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
}
