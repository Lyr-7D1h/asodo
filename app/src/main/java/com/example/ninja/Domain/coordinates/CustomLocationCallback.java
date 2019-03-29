package com.example.ninja.Domain.coordinates;

import android.location.Location;

import com.example.ninja.Controllers.LocationService;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.trips.Trip;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;

public class CustomLocationCallback extends LocationCallback {

    private LocationService locationService;
    private Trip currentTrip;
    private float lastEstimation;

    public CustomLocationCallback(LocationService locationService, Trip currentTrip) {
        this.locationService = locationService;
        this.currentTrip = currentTrip;
        this.lastEstimation = 0.f;
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        if (locationResult == null) {
            return;
        }

        for (Location location : locationResult.getLocations()) {
            this.currentTrip.addLocation(location);

            // Update if estimation changed
            if(this.lastEstimation < this.currentTrip.getEstimatedKMDrivenf()) {
                this.lastEstimation = this.currentTrip.getEstimatedKMDrivenf();

                this.locationService.broadcastEstimation();
                this.locationService.updateNotification();
            }

            System.out.println(location);
            System.out.println(location.getAccuracy());
            System.out.println(((Global) this.locationService.getApplication()).isActiveTrip());
            System.out.println("Current list: " + this.currentTrip.getLocationList().getLocationsSize());
        }
    }
}
