package com.example.ninja.Domain;

import android.app.Activity;
import android.content.Context;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private int businessTrip = 1;
    private String startcity;
    private String endcity;
    private String afwijking;
    private String reistijd;

    public Trip(Context ctx){
        this.userID = UserUtils.getUserID(ctx);
        this.carID = UserUtils.getFirstCarID(ctx);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        this.date = dateFormat.format(date);

        this.tripStarted = dateFormat.format(date);
    }

    public Trip(String tripID, String userID, String carID, int mileageStarted, int mileageEnded, String date, String tripStarted, String tripEnded, int businessTrip) {
        this.tripID = tripID;
        this.userID = userID;
        this.carID = carID;
        this.mileageStarted = mileageStarted;
        this.mileageEnded = mileageEnded;
        this.date = date;
        this.tripStarted = tripStarted;
        this.tripEnded = tripEnded;
        this.businessTrip = businessTrip;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public String getCarID() {
        return carID;
    }

    public void setMileageStarted(int mileageStarted) {
        this.mileageStarted = mileageStarted;
    }

    public void setMileageEnded(int mileageEnded){
        this.mileageEnded = mileageEnded;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        this.tripEnded = dateFormat.format(date);
    }

    public JsonObject getVals(){
        // Init
        JsonObject res = new JsonObject();

        // Add properties
        res.add("id", new JsonPrimitive(this.tripID));
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
        AsodoRequester.newRequest("registerTrip", getVals(), ctx, new CustomListener() {
            @Override
            public void onResponse(JsonObject jsonResponse) {
                setTripID(jsonResponse.get("tripID").getAsString());
            }
        });
    }

    public static Trip build(JsonObject tripJson) {
        Trip res = new Trip(
                tripJson.get("id").getAsString(),
                tripJson.get("userID").getAsString(),
                tripJson.get("carID").getAsString(),
                tripJson.get("mileageStarted").getAsInt(),
                tripJson.get("mileageEnded").getAsInt(),
                tripJson.get("tripStarted").getAsString(),
                tripJson.get("tripEnded").getAsString(),
                tripJson.get("date").getAsString(),
                tripJson.get("businessTrip").getAsInt()
        );

        return res;
    }

    public void builder(Context ctx){
        CacheUtils.cacheObject(ctx, this, "trips.list");
    }



}
