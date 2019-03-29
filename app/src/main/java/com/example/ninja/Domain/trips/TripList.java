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

    public TripList(JsonObject trips) {
        this.trips = trips;
    }

    public JsonArray getTrips() {
        if(trips == null) {
            this.trips = new JsonObject();
            this.trips.add("trips", new JsonArray());
        }
        return trips.getAsJsonArray("trips");
    }

    public void addTrip(Trip trip) {
        this.trips.getAsJsonArray("trips").add(trip.toJsonObject());
    }

    public void cache(Context ctx) {
        CacheUtils.cacheJsonObject(ctx, 0, this.trips, "trips.list");
    }

    public JsonObject toJsonObject() {
        return trips;
    }

    public static TripList build(Context ctx) {
        try {
            JsonObject tripsJson = CacheUtils.readCache(ctx, "trips.list");
            TripList res = new TripList();

            if (tripsJson == null) {
                throw new MalformedJsonException("Missing trips argument");
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
