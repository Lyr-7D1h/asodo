package com.example.ninja.Controllers.Routetracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Controllers.abstractActivities.PermissionActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.Domain.util.ActivityUtils;
import com.example.ninja.Domain.util.PermissionUtils;
import com.example.ninja.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Objects;

public class Startroute extends PermissionActivity {

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_route);

        // Redirect
        if(checkActiveTrip()) {
            return;
        }

        // Init
        Trip currentTrip = new Trip(context);
        ((Global) this.getApplication()).setTrip(currentTrip);

        // Set disabled
        findViewById(R.id.startkm).setEnabled(false);
        findViewById(R.id.start).setEnabled(false);

        // Check location permission
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        currentTrip.setTrackingSetting(Integer.parseInt(Objects.requireNonNull(prefs.getString("tracking_setting", "2"))));
        if(currentTrip.getTrackingSetting() > 0) {
            if (PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, this.get_REQUEST_CODE_FINE_LOCATION())) {
                permissionAccepted(this.get_REQUEST_CODE_FINE_LOCATION());
            }
        } else {
            permissionDeclined(this.get_REQUEST_CODE_FINE_LOCATION());
        }
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

    private void initTrip(int lastMileage) {
        // Init
        Trip currentTrip = ((Global) getApplication()).getTrip();

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
        // Init
        Trip currentTrip = ((Global) getApplication()).getTrip();

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
        Trip currentTrip = ((Global) getApplication()).getTrip();

        // Set values
        currentTrip.setTripStarted();
        currentTrip.setBusinessTrip(((Switch) findViewById(R.id.businessTrip)).isChecked() ?1:0);
        currentTrip.setBbCommuting(((Switch) findViewById(R.id.bbCommuting)).isChecked() ?0:1);

        // Get start city
        if(currentTrip.getTrackingSetting() == 0) {
            String cityEntered = ((EditText) findViewById(R.id.enterStartCityET)).getText().toString();
            if(!cityEntered.isEmpty()) {
                currentTrip.setCityStarted(cityEntered);
            } else {
                Toast.makeText(Startroute.this, getString(R.string.enter_city), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Validate start mileage
        String startMileage = ((EditText) findViewById(R.id.startkm)).getText().toString();
        if(!validMileage(startMileage, lastMileage)) {
            return false;
        }

        currentTrip.setMileageStarted(Integer.parseInt(startMileage));
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

    public boolean checkActiveTrip() {
        if(((Global) this.getApplication()).isActiveTrip()) {
            ActivityUtils.changeActivity(this, Startroute.this, Route.class);
            finish();
            return true;
        }

        return false;
    }

    @Override
    public void permissionAccepted(int requestCode) {
        // Get mileage
        getLastMileage();
    }

    @Override
    public void permissionDeclined(int requestCode) {
        // Reset tracking setting preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("tracking_setting", "0");
        editor.apply();

        // Reset tracking setting in trip
        Trip currentTrip = ((Global) this.getApplication()).getTrip();
        currentTrip.setTrackingSetting(0);

        // Show start city input
        findViewById(R.id.enterStartCityCont).setVisibility(View.VISIBLE);

        // Get mileage
        getLastMileage();
    }
}

