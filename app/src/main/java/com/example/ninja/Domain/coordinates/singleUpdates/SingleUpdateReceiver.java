package com.example.ninja.Domain.coordinates.singleUpdates;

import android.location.Location;

public abstract class SingleUpdateReceiver {
    public abstract void onLocation(Location location);
}
