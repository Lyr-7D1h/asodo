package com.example.ninja.Domain.coordinates;

import android.location.Location;

import com.example.ninja.Controllers.LocationService;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.Trip;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class CustomLocationCallback extends LocationCallback {

    private LocationService locationService;
    private Trip currentTrip;

    public CustomLocationCallback(LocationService locationService, Trip currentTrip) {
        this.locationService = locationService;
        this.currentTrip = currentTrip;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            return;
        }

        for (Location location : locationResult.getLocations()) {
            this.currentTrip.addLocation(location);
            this.locationService.broadcastEstimation();

            System.out.println(location);
            System.out.println(location.getAccuracy());
            System.out.println(((Global) this.locationService.getApplication()).isActiveTrip());
            System.out.println("Current list: " + this.currentTrip.getLocationList().getLocationsSize());
        }
    }
}
