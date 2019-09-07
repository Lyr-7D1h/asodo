package com.example.ninja.Domain.managers;

import android.content.Intent;

import com.example.ninja.Domain.trips.Trip;

public class ActiveTripManager {
    private boolean activeTrip;
    private int tripStatus;
    private Trip trip;
    private Intent locationIntent;

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
}
