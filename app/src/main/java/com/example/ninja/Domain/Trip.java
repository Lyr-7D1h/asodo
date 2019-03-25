package com.example.ninja.Domain;

import android.content.Context;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.ninja.Domain.util.CacheUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class Trip implements Serializable {

    private int startMileage = 0;
    private int endMileage = 0;
    private String date     = "";
    private String startDate = "";
    private String endDate = "";

    public Trip(){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        this.date = dateFormat.format(date);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.startDate = dateFormat.format(date);
    }

    public void setStartMileage(int startMileage) {
        this.startMileage = startMileage;
    }

    public void setEndMileage(int endMileage){
        this.endMileage = endMileage;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        this.endDate = dateFormat.format(date);
    }

    public JsonObject getVals(){
        // Init
        JsonObject res = new JsonObject();

        // Add properties
        res.add("startMileage", new JsonPrimitive(this.startMileage));
        res.add("endMileage", new JsonPrimitive(this.endMileage));
        res.add("startDate", new JsonPrimitive(this.startDate));
        res.add("endDate", new JsonPrimitive(this.endDate));
        res.add("date", new JsonPrimitive(this.date));

        // Return
        return res;
    }

    public void builder(Context ctx){
        CacheUtils.cacheObject(ctx, this, "trips.list");
    }
}
