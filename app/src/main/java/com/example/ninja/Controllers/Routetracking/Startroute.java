package com.example.ninja.Controllers.Routetracking;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.Domain.util.ActivityUtils;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Domain.util.ConnectivityUtils;
import com.example.ninja.Domain.util.UserUtils;
import com.example.ninja.R;
import com.google.gson.JsonArray;
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

        checkActiveTrip();

        // Init
        currentTrip = new Trip(context);
        ((Global) this.getApplication()).setTrip(currentTrip);

        // Set disabled
        findViewById(R.id.startkm).setEnabled(false);
        findViewById(R.id.start).setEnabled(false);

        // Get mileage
        getLastMileage();
    }

    private void getLastMileage() {
        ((Global) this.getApplication()).getTripCache(new AsodoRequesterCallback() {
            @Override
            public void callback(JsonObject jsonResponse) {
                JsonArray cachedTrips = new TripList(jsonResponse).getTrips();

                if(cachedTrips.size() > 0) {
                    Trip lastCachedTrip = Trip.build(cachedTrips.get(cachedTrips.size() - 1).getAsJsonObject());
                    int lastCachedMileage = lastCachedTrip.getMileageEnded();

                    initTrip(lastCachedMileage);
                } else {
                    Toast.makeText(Startroute.this, "Kan laatste kilometerstand niet laden!", Toast.LENGTH_SHORT).show();
                    initTrip(0);
                }
            }
        });
    }

    private void initTrip(int lastMileage) {
        // Init start mileage
        currentTrip.setMileageStarted(lastMileage);
        ((TextView) findViewById(R.id.startkm)).setText(String.valueOf(lastMileage));
        if(lastMileage == 0) {
            ((TextView) findViewById(R.id.confirmTV)).setText(String.valueOf("Vul kilometerstand in"));
        }
        findViewById(R.id.startkm).setEnabled(true);

        // Start button
        Activity self = this;
        final Button button = findViewById(R.id.start);
        button.setEnabled(true);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check for corrupt Trip
                if(currentTrip.getCarID().isEmpty()) {
                    // Show toast
                    Toast.makeText(Startroute.this, "Geen auto voor route geselecteerd!", Toast.LENGTH_SHORT).show();

                    // Return
                    return;
                }

                // Update trip
                currentTrip = ((Global) getApplication()).getTrip();
                currentTrip.setTripStarted();
                // TODO set businessTrip, bbComuting
                currentTrip.setTrackingSetting(1); // TODO
                currentTrip.setMileageStarted(Integer.parseInt(((TextView) findViewById(R.id.startkm)).getText().toString())); //TODO validate

                // Move to next activity
                Intent intent = new Intent(v.getContext(), Route.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void checkActiveTrip() {
        if(((Global) this.getApplication()).isActiveTrip()) {
            ActivityUtils.changeActivity(this, Startroute.this, Route.class);
            finish();
        }
    }
}

