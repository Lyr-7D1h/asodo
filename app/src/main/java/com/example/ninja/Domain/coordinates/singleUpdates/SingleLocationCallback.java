package com.example.ninja.Domain.coordinates.singleUpdates;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class SingleLocationCallback extends LocationCallback {

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SingleUpdateReceiver singleUpdateReceiver;

    public SingleLocationCallback(FusedLocationProviderClient mFusedLocationProviderClient, SingleUpdateReceiver singleUpdateReceiver) {
        this.mFusedLocationProviderClient = mFusedLocationProviderClient;
        this.singleUpdateReceiver = singleUpdateReceiver;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if(locationResult == null) {
            return;
        }

        Location location = locationResult.getLastLocation();
        System.out.println(location.getAccuracy());

        if(location.getAccuracy() <= 25.f) {
            singleUpdateReceiver.onLocation(location);
            mFusedLocationProviderClient.removeLocationUpdates(this);
        }
    }
}
