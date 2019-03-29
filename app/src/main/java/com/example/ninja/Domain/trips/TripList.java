package com.example.ninja.Domain.trips;

import android.content.Context;

import com.example.ninja.Domain.util.CacheUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.MalformedJsonException;

public class TripList {

    private JsonObject trips;

    public TripList() {
        this.trips = new JsonObject();
        this.trips.add("trips", new JsonArray());
    }

    public JsonArray getTrips() {
        return trips.getAsJsonArray("trips");
    }

    public void addTrip(Trip trip) {
        this.trips.getAsJsonArray("trips").add(trip.getVals());
    }

    public void cache(Context ctx) {
        CacheUtils.cacheJsonObject(ctx, 0, this.trips, "trips.list");
    }


    public static TripList build(Context ctx) {
        try {
            JsonObject tripsJson = CacheUtils.readCache(ctx, "trips.list");
            TripList res = new TripList();

            if (tripsJson == null) {
                return res;
            }

            JsonArray tripsArray = tripsJson.getAsJsonArray("trips");
            for (int i = 0; i < tripsArray.size(); i++) {
                JsonObject tripJson = tripsArray.get(i).getAsJsonObject();
                res.addTrip(Trip.build(tripJson));
            }

            return res;
        } catch (MalformedJsonException e) {
            CacheUtils.deleteCache(ctx, "trips.list");
            return new TripList();
        }
    }
}
