package com.example.ninja.Domain.coordinates;

import android.location.Location;

import java.io.Serializable;
import java.util.ArrayList;

public class LocationList implements Serializable {
    private ArrayList<Location> locations;

    public LocationList() {
        this.locations = new ArrayList<>();
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public int getLocationsSize() {
        return locations.size();
    }

    public void addLocation(Location location) {
        getLocations().add(location);
    }
}
