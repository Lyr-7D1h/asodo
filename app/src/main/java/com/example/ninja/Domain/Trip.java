package com.example.ninja.Domain;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.Parcelable;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.ninja.Domain.coordinates.LocationList;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Domain.util.UserUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Trip implements Serializable {

    private String tripID     = "";
    private String userID     = "";
    private String carID     = "";
    private int mileageStarted = 0;
    private int mileageEnded = 0;
    private String date     = "";
    private String tripStarted = "";
    private String tripEnded = "";
    private Location locationStarted;
    private Location locationEnded;
    private int businessTrip = 1;
    private LocationList locationList;
    private float estimatedDistanceDriven = 0;

    public Trip(Context ctx){
        this.userID = UserUtils.getUserID(ctx);
        this.carID = UserUtils.getFirstCarID(ctx);

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        this.date = dateFormat.format(date);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.tripStarted = dateFormat.format(date);

        this.locationList = new LocationList();
    }

    public String getCarID() {
        return carID;
    }

    public int getMileageStarted() {
        return mileageStarted;
    }

    public void setMileageStarted(int mileageStarted) {
        this.mileageStarted = mileageStarted;
    }

    public void setMileageEnded(int mileageEnded){
        this.mileageEnded = mileageEnded;
    }

    public void setTripEnded() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        this.tripEnded = dateFormat.format(date);
    }

    public void setLocationStarted(Location locationStarted) {
        this.locationStarted = locationStarted;
    }

    public void setLocationEnded(Location locationEnded) {
        this.locationEnded = locationEnded;
    }

    public float getEstimatedKMDrivenf() {
        return Math.round((estimatedDistanceDriven) / 100.f) / 10.f;
    }

    public int getEstimatedKMDriven() {
        return Math.round(estimatedDistanceDriven / 1000);
    }

    public LocationList getLocationList() {
        return locationList;
    }

    public void addLocation(Location location) {
        getLocationList().addLocation(location);

        if(getLocationList().getLocationsSize() >= 2) {
            estimatedDistanceDriven += getLocationList().getLocations().get(getLocationList().getLocationsSize()-2).distanceTo(getLocationList().getLocations().get(getLocationList().getLocationsSize()-1));
        }
    }

    public JsonObject getVals(){
        // Init
        JsonObject res = new JsonObject();

        // Add properties
        res.add("userID", new JsonPrimitive(this.userID));
        res.add("carID", new JsonPrimitive(this.carID));
        res.add("mileageStarted", new JsonPrimitive(this.mileageStarted));
        res.add("mileageEnded", new JsonPrimitive(this.mileageEnded));
        res.add("tripStarted", new JsonPrimitive(this.tripStarted));
        res.add("tripEnded", new JsonPrimitive(this.tripEnded));
        res.add("date", new JsonPrimitive(this.date));
        res.add("businessTrip", new JsonPrimitive(this.businessTrip));

        // Return
        return res;
    }

    public void registerToDB(Activity ctx) {
        Trip self = this;
        AsodoRequester.newRequest("registerTrip", getVals(), ctx, new CustomListener() {
            @Override
            public void onResponse(JsonObject jsonResponse) {
                tripID = jsonResponse.get("tripID").getAsString();
            }
        });
    }

    public void builder(Context ctx){
        CacheUtils.cacheObject(ctx, this, "trips.list");
    }
}
