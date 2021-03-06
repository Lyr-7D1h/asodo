package com.example.ninja.Domain.trips;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.ninja.Domain.coordinates.LatLngList;
import com.example.ninja.Domain.coordinates.LocationList;
import com.example.ninja.Domain.coordinates.singleUpdates.LatLngWrapper;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.util.Callback;
import com.example.ninja.Domain.util.UserUtils;
import com.example.ninja.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Trip implements Serializable {

    private String tripID;
    private String userID;
    private String carID;
    private int mileageStarted;
    private int mileageEnded;
    private int distanceDriven;
    private String tripStarted;
    private String tripEnded;
    private long tripDuration;
    private String cityStarted;
    private String cityEnded;
    private String routePolyline;
    private int businessTrip;
    private int bbCommuting;
    private String desDeviation;
    private int trackingSetting;
    private float estimatedDistanceDriven;
    private float optimalDistance;
    private float kmDeviation;

    private LocationList locationList;
    private DateFormat dateFormat;

    public Trip(Context ctx){
        this.userID = UserUtils.getUserID(ctx);
        this.carID = UserUtils.getFirstCarID(ctx);
        this.estimatedDistanceDriven = 0.f;

        this.locationList = new LocationList();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public Trip() {
        this.locationList = new LocationList();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public String getTripID() {
        return tripID;
    }


    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getCarID() {
        return carID;
    }

    public void setCarID(String carID) {
        this.carID = carID;
    }

    public int getMileageStarted() {
        return mileageStarted;
    }

    public void setMileageStarted(int mileageStarted) {
        this.mileageStarted = mileageStarted;
    }

    public int getMileageEnded() {
        return mileageEnded;
    }

    public void setMileageEnded(int mileageEnded) {
        this.mileageEnded = mileageEnded;

        setDistanceDriven(this.mileageEnded-this.mileageStarted);
    }

    public int getDistanceDriven() {
        return distanceDriven;
    }

    public void setDistanceDriven(int distanceDriven) {
        this.distanceDriven = distanceDriven;
    }

    public String getTripStarted() {
        return tripStarted;
    }

    public void setTripStarted(String tripStarted) {
        this.tripStarted = tripStarted;
    }

    public void setTripStarted() {
        if(this.tripStarted == null) {
            Date date = new Date();

            this.tripStarted = dateFormat.format(date);
        }
    }

    public String getTripEnded() {
        return tripEnded;
    }

    public void setTripEnded(String tripEnded) {
        this.tripEnded = tripEnded;
    }

    public void setTripEnded() {
        if(this.tripEnded == null) {
            Date date = new Date();

            this.tripEnded = dateFormat.format(date);

            // Set duration
            setTripDuration();
        }
    }

    public long getTripDuration() {
        return tripDuration;
    }

    public void setTripDuration(long tripDuration) {
        this.tripDuration = tripDuration;
    }

    private void setTripDuration() {
        try {
            Date start = dateFormat.parse(this.tripStarted);
            Date end = dateFormat.parse(this.tripEnded);

            this.tripDuration = Math.abs(end.getTime() - start.getTime());;
        } catch (ParseException e) {
            // Do nothing
        }
    }

    public String getCityStarted() {
        return cityStarted;
    }

    public void setCityStarted(String cityStarted) {
        this.cityStarted = cityStarted;
    }

    public String getCityEnded() {
        return cityEnded;
    }

    public void setCityEnded(String cityEnded) {
        this.cityEnded = cityEnded;
    }

    public String getRoutePolyline() {
        return routePolyline;
    }

    public void setRoutePolyline(String routePolyline) {
        this.routePolyline = routePolyline;
    }

    public int getBusinessTrip() {
        return businessTrip;
    }

    public void setBusinessTrip(int businessTrip) {
        this.businessTrip = businessTrip;
    }

    public int getBbCommuting() {
        return bbCommuting;
    }

    public void setBbCommuting(int bbCommuting) {
        this.bbCommuting = bbCommuting;
    }

    public String getDesDeviation() {
        return desDeviation;
    }

    public void setDesDeviation(String desDeviation) {
        this.desDeviation = desDeviation;
    }

    public int getTrackingSetting() {
        return trackingSetting;
    }

    public void setTrackingSetting(int trackingSetting) {
        this.trackingSetting = trackingSetting;
    }

    public float getEstimatedDistanceDriven() {
        return estimatedDistanceDriven;
    }

    public void setEstimatedDistanceDriven(float estimatedDistanceDriven) {
        this.estimatedDistanceDriven = estimatedDistanceDriven;
    }

    public float getOptimalDistance() {
        return optimalDistance;
    }

    public void setOptimalDistance(float optimalDistance) {
        this.optimalDistance = optimalDistance;
    }

    public float getKmDeviation() {
        return kmDeviation;
    }

    public void setKmDeviation(float kmDeviation) {
        this.kmDeviation = kmDeviation;
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
        if(location != null) {
            getLocationList().addLocation(location);

            if (getLocationList().getLocationsSize() >= 2) {
                estimatedDistanceDriven += getLocationList().getLocations().get(getLocationList().getLocationsSize() - 2).distanceTo(getLocationList().getLocations().get(getLocationList().getLocationsSize() - 1));
            }
        }
    }

    public JsonObject toJsonObject() {
        // Init
        JsonObject res = new JsonObject();

        // Add attributes
        if(this.tripID != null) {
            res.add("tripID", new JsonPrimitive(this.tripID));
        } else {
            res.add("tripId", new JsonPrimitive(""));
        }

        if(this.userID != null) {
            res.add("userID", new JsonPrimitive(this.userID));
        } else {
            res.add("userID", new JsonPrimitive(""));
        }

        if(this.carID != null) {
            res.add("carID", new JsonPrimitive(this.carID));
        } else {
            res.add("", new JsonPrimitive(""));
        }
        res.add("mileageStarted", new JsonPrimitive(this.mileageStarted));
        res.add("mileageEnded", new JsonPrimitive(this.mileageEnded));
        res.add("distanceDriven", new JsonPrimitive(this.distanceDriven));

        if(this.tripStarted != null) {
            res.add("tripStarted", new JsonPrimitive(this.tripStarted));
        } else {
            res.add("tripStarted", new JsonPrimitive(""));
        }

        if(this.tripEnded != null) {
            res.add("tripEnded", new JsonPrimitive(this.tripEnded));
        } else {
            res.add("tripEnded", new JsonPrimitive(""));
        }

        res.add("tripDuration", new JsonPrimitive(this.tripDuration));

        if(this.cityStarted != null) {
            res.add("cityStarted", new JsonPrimitive(this.cityStarted));
        } else {
            res.add("cityStarted", new JsonPrimitive(""));
        }

        if(this.cityEnded != null) {
            res.add("cityEnded", new JsonPrimitive(this.cityEnded));
        } else {
            res.add("cityEnded", new JsonPrimitive(""));
        }

        if(this.routePolyline != null) {
            res.add("routePolyline", new JsonPrimitive(this.routePolyline));
        } else {
            res.add("routePolyline", new JsonPrimitive(""));
        }



        res.add("businessTrip", new JsonPrimitive(this.businessTrip));
        res.add("bbCommuting", new JsonPrimitive(this.bbCommuting));

        if(this.desDeviation != null) {
            res.add("desDeviation", new JsonPrimitive(this.desDeviation));
        } else {
            res.add("desDeviation", new JsonPrimitive(""));
        }

        res.add("trackingSetting", new JsonPrimitive(this.trackingSetting));
        res.add("estimatedDistanceDriven", new JsonPrimitive(this.estimatedDistanceDriven));
        res.add("optimalDistanceDriven", new JsonPrimitive(this.estimatedDistanceDriven));
        res.add("kmDeviation", new JsonPrimitive(this.kmDeviation));

        // Return
        return res;
    }

    public void registerToDB(Context ctx, Callback callback) {
        // Set city started and ended by location
        if(getTrackingSetting() > 0) {
            LatLngList latLngList = LatLngList.decode(getRoutePolyline());

            setCityStarted(new LatLngWrapper(latLngList.get(0)).getCity(ctx));
            setCityEnded(new LatLngWrapper(latLngList.get(latLngList.size() - 1)).getCity(ctx));
        }

        AsodoRequester.newRequest("registerTrip", toJsonObject(), ctx, new CustomListener() {
            @Override
            public void onResponse(JsonObject jsonResponse) {
                // Set tripID
                setTripID(jsonResponse.get("tripID").getAsString());

                // Callback
                callback.callback();
            }
        });
    }

    public static Trip build(JsonObject jsonObject) {
        // Init
        Trip res = new Trip();

        // Fill object
        if(jsonObject.has("tripID") && jsonObject.get("tripID") != null) {
            res.setTripID(jsonObject.get("tripID").getAsString());
        }

        if(jsonObject.has("userID") && jsonObject.get("userID") != null) {
            res.setUserID(jsonObject.get("userID").getAsString());
        }

        if(jsonObject.has("carID") && jsonObject.get("carID") != null) {
            res.setCarID(jsonObject.get("carID").getAsString());
        }

        if(jsonObject.has("mileageStarted") && jsonObject.get("mileageStarted") != null) {
            res.setMileageStarted(jsonObject.get("mileageStarted").getAsInt());
        }

        if(jsonObject.has("mileageEnded") && jsonObject.get("mileageEnded") != null) {
            res.setMileageEnded(jsonObject.get("mileageEnded").getAsInt());
        }

        if(jsonObject.has("distanceDriven") && jsonObject.get("distanceDriven") != null) {
            res.setDistanceDriven(jsonObject.get("distanceDriven").getAsInt());
        }

        if(jsonObject.has("tripStarted") && jsonObject.get("tripStarted") != null) {
            res.setTripStarted(jsonObject.get("tripStarted").getAsString());
        }

        if(jsonObject.has("tripEnded") && jsonObject.get("tripEnded") != null) {
            res.setTripEnded(jsonObject.get("tripEnded").getAsString());
        }

        if(jsonObject.has("tripDuration") && jsonObject.get("tripDuration") != null) {
            res.setTripDuration(jsonObject.get("tripDuration").getAsLong());
        }

        if(jsonObject.has("cityStarted") && jsonObject.get("cityStarted") != null) {
            res.setCityStarted(jsonObject.get("cityStarted").getAsString());
        }

        if(jsonObject.has("cityEnded") && jsonObject.get("cityEnded") != null) {
            res.setCityEnded(jsonObject.get("cityEnded").getAsString());
        }

        if(jsonObject.has("routePolyline") && jsonObject.get("routePolyline") != null) {
            res.setRoutePolyline(jsonObject.get("routePolyline").getAsString());
        }

        if(jsonObject.has("businessTrip") && jsonObject.get("businessTrip") != null) {
            res.setBusinessTrip(jsonObject.get("businessTrip").getAsInt());
        }

        if(jsonObject.has("bbCommuting") && jsonObject.get("bbCommuting") != null) {
            res.setBbCommuting(jsonObject.get("bbCommuting").getAsInt());
        }

        if(jsonObject.has("desDeviation") && jsonObject.get("desDeviation") != null) {
            res.setDesDeviation(jsonObject.get("desDeviation").getAsString());
        }

        if(jsonObject.has("trackingSetting") && jsonObject.get("trackingSetting") != null) {
            res.setTrackingSetting(jsonObject.get("trackingSetting").getAsInt());
        }

        if(jsonObject.has("estimatedDistanceDriven") && jsonObject.get("estimatedDistanceDriven") != null) {
            res.setEstimatedDistanceDriven(jsonObject.get("estimatedDistanceDriven").getAsFloat());
        }

        if(jsonObject.has("optimalDistance") && jsonObject.get("optimalDistance") != null) {
            res.setOptimalDistance(jsonObject.get("optimalDistance").getAsFloat());
        }

        if(jsonObject.has("kmDeviation") && jsonObject.get("kmDeviation") != null) {
            res.setKmDeviation(jsonObject.get("kmDeviation").getAsFloat());
        }

        // Return
        return res;
    }

}
