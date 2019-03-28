package com.example.ninja.Domain;

import android.app.Application;
import android.content.Intent;

public class Global extends Application {
    private boolean activeTrip;
    private Trip trip;
    private Intent locationIntent;

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
