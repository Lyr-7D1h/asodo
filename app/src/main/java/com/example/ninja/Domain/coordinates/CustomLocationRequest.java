package com.example.ninja.Domain.coordinates;

import com.google.android.gms.location.LocationRequest;

public class CustomLocationRequest {
    private LocationRequest locationRequest;

    public CustomLocationRequest(long minTime, long minDistance) {
        this.locationRequest = new LocationRequest();

        // Init values
        this.locationRequest.setFastestInterval(minTime);
        this.locationRequest.setMaxWaitTime(minTime);
        this.locationRequest.setInterval(minTime);
        this.locationRequest.setSmallestDisplacement(minDistance);
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public LocationRequest getLocationRequest() {
        return locationRequest;
    }
}
