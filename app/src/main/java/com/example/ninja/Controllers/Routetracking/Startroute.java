package com.example.ninja.Controllers.Routetracking;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.Domain.util.ActivityUtils;
import com.example.ninja.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Startroute extends BackButtonActivity {

    private final Context context = this;
    private Trip currentTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_route);

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
                    Toast.makeText(Startroute.this, getString(R.string.start_route_no_last_mileage), Toast.LENGTH_SHORT).show();
                    initTrip(0);
                }
            }
        });
    }

    private void initInputs() {

    }

    private void initTrip(int lastMileage) {
        // Init start mileage
        currentTrip.setMileageStarted(lastMileage);
        ((TextView) findViewById(R.id.startkm)).setText(String.valueOf(lastMileage));
        if(lastMileage == 0) {
            ((TextView) findViewById(R.id.confirmTV)).setText(String.valueOf(getString(R.string.start_route_no_last_mileage_helper)));
        }
        findViewById(R.id.startkm).setEnabled(true);

        // Business switch trip
        Switch businessTripSwitch = findViewById(R.id.businessTrip);
        businessTripSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConstraintLayout bbCommutingCont = findViewById(R.id.bbCommutingCont);
            if(businessTripSwitch.isChecked()) {
                ((Switch) findViewById(R.id.bbCommuting)).setChecked(true);
                bbCommutingCont.setVisibility(View.VISIBLE);
            } else {
                bbCommutingCont.setVisibility(View.GONE);
            }
        });

        // Start button
        final Button startButton = findViewById(R.id.start);
        startButton.setEnabled(true);
        startButton.setOnClickListener(v -> onStartButtonClick(v, lastMileage));
    }

    public void onStartButtonClick(View v, int lastMileage) {
        // Check for corrupt Trip
        if(currentTrip.getCarID().isEmpty()) {
            // Show toast
            Toast.makeText(Startroute.this, getString(R.string.start_route_no_car), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update trip
        if(updateTrip(lastMileage)) {
            // Move to next activity
            Intent intent = new Intent(v.getContext(), Route.class);
            startActivity(intent);
            finish();
        }
    }

    public boolean updateTrip(int lastMileage) {
        // Init
        currentTrip = ((Global) getApplication()).getTrip();

        // Set values
        currentTrip.setTripStarted();
        currentTrip.setTrackingSetting(2); // TODO
        currentTrip.setBusinessTrip(((Switch) findViewById(R.id.businessTrip)).isChecked() ?1:0);
        currentTrip.setBbCommuting(((Switch) findViewById(R.id.bbCommuting)).isChecked() ?0:1);

        // Validate start mileage
        String startMileage = ((EditText) findViewById(R.id.startkm)).getText().toString();
        if(!validMileage(startMileage, lastMileage)) {
            return false;
        }

        currentTrip.setMileageStarted(Integer.parseInt(startMileage)); //TODO validate
        return true;
    }

    public boolean validMileage(String startMileage, int lastMileage) {
        try {
            int res = Integer.parseInt(startMileage);

            if(res < lastMileage) {
                Toast.makeText(Startroute.this, getString(R.string.start_route_mileage_lower), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(Startroute.this, getString(R.string.mileage_no_number), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void checkActiveTrip() {
        if(((Global) this.getApplication()).isActiveTrip()) {
            ActivityUtils.changeActivity(this, Startroute.this, Route.class);
            finish();
        }
    }
}

