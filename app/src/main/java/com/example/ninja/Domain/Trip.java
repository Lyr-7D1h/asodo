package com.example.ninja.Domain;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Trip implements Serializable {

    private String start    = "";
    private String end      = "";
    private String date     = "";
    private String timdate = "";

    public Trip(String start){
        this.start = start;

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        Date date = new Date();
        this.date = dateFormat.format(date);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.timdate = dateFormat.format(date);
    }

    public void setEnd(String end){
        this.end = end;
    }

    public String[] getVals(){
        String[] vals = {this.start, this.end, this.date, this.timdate};
        return vals;
    }





    public void builder(Context ctx){
        FileOutputStream outputStream;


        final GsonBuilder gbuilder = new GsonBuilder();
        final Gson gson = gbuilder.create();

        String json = gson.toJson(this);
        System.out.println(json);


        try {
            outputStream = ctx.openFileOutput("trips.list", Context.MODE_APPEND);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
