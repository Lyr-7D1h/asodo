package com.example.ninja.Controllers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ninja.Domain.Trip;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.util.UserUtils;
import com.example.ninja.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Startroute extends AppCompatActivity {

    private final Context context = this;
    private Trip currentTrip;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_route);

        // Make request for last mileage
        getLastMileage();
    }

    private void getLastMileage() {
        // Get user ID
        String userID = UserUtils.getUserID(context);

        // Make request
        String jsonString = "{"
                + "\"userID\":" + userID + ","
                + "\"limit\":1"
                + "}";
        System.out.println(jsonString);
        JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();

        AsodoRequester.newRequest("getTrips", json, Startroute.this, new CustomListener() {
            @Override
            public void onResponse(JsonObject jsonResponse) {
                lastMileageResponseListener(jsonResponse);
            }
        });
    }

    private void lastMileageResponseListener(JsonObject jsonResponse) {
        System.out.println(jsonResponse);
        initTrip(0);
    }

    private void initTrip(int lastMileage) {
        // Init start mileage
        currentTrip = new Trip();
        currentTrip.setStartMileage(lastMileage);
        ((TextView) findViewById(R.id.startkm)).setText(String.valueOf(lastMileage));

        // Start button
        final Button button = findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Route.class);
                intent.putExtra("km", currentTrip);
                startActivity(intent);
            }
        });
    }
}

