package com.example.ninja.Domain.coordinates.singleUpdates;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.example.ninja.Domain.coordinates.CustomLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.OnSuccessListener;

public class SingleUpdateProvider {

    private Context context;
    private FusedLocationProviderClient mFusedLocationClient;
    private SingleLocationCallback singleLocationCallback;

    public SingleUpdateProvider(Context context, FusedLocationProviderClient mFusedLocationClient) {
        this.context = context;
        this.mFusedLocationClient = mFusedLocationClient;
    }

    public void requestSingleUpdate(SingleUpdateReceiver singleUpdateReceiver) {
        System.out.println("Getting single location!");
        this.singleLocationCallback = new SingleLocationCallback(mFusedLocationClient, singleUpdateReceiver);
        getLocation();
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationRequest locationRequest = new CustomLocationRequest(500,0).getLocationRequest();
        mFusedLocationClient.requestLocationUpdates(locationRequest, singleLocationCallback, null);
    }
}
