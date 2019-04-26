package com.example.ninja.Controllers.Routetracking;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
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
import com.example.ninja.Domain.util.AlertUtils;
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
        ((Global) this.getApplication()).getActiveTripManager().setTrip(currentTrip);

        // Init number picker
        ((NumberPicker) findViewById(R.id.npStart)).setWrapSelectorWheel(false);
        ((NumberPicker) findViewById(R.id.npStart)).setMinValue(0);
        ((NumberPicker) findViewById(R.id.npStart)).setMaxValue(1000000);

        // Set disabled
        findViewById(R.id.npStart).setEnabled(false);
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
        ((Global) this.getApplication()).getSyncManager().getTripCache(new AsodoRequesterCallback() {
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
        Trip currentTrip = ((Global) getApplication()).getActiveTripManager().getTrip();

        // Init start mileage
        currentTrip.setMileageStarted(lastMileage);

        ((NumberPicker) findViewById(R.id.npStart)).setValue(lastMileage);
        findViewById(R.id.npStart).setEnabled(true);

        if(lastMileage == 0) {
            ((TextView) findViewById(R.id.confirmTV)).setText(String.valueOf(getString(R.string.start_route_no_last_mileage_helper)));
        }

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
        Trip currentTrip = ((Global) getApplication()).getActiveTripManager().getTrip();

        // Check for corrupt Trip
        if(currentTrip.getCarID().isEmpty()) {
            // Show toast
            Toast.makeText(Startroute.this, getString(R.string.start_route_no_car), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update trip
        updateTrip(lastMileage, new ValidationCallback() {
            @Override
            public void callback(boolean valid) {
                if(valid) {
                    // Move to next activity
                    Intent intent = new Intent(v.getContext(), Route.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void updateTrip(int lastMileage, ValidationCallback callback) {
        // Init
        Trip currentTrip = ((Global) getApplication()).getActiveTripManager().getTrip();

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
                callback.callback(false);
                return;
            }
        }

        // Validate start mileage
        String startMileage = String.valueOf(((NumberPicker) findViewById(R.id.npStart)).getValue());
        validMileage(startMileage, lastMileage, new ValidationCallback() {
            @Override
            public void callback(boolean valid) {
                if(valid) {
                    currentTrip.setMileageStarted(Integer.parseInt(startMileage));
                    callback.callback(true);
                }
            }
        });
    }

    public void validMileage(String startMileage, int lastMileage, ValidationCallback callback) {
        try {
            int res = Integer.parseInt(startMileage);

            if(res < lastMileage) {
                AlertUtils.showAlert(getString(R.string.proceed), getString(R.string.cancel), getString(R.string.start_route_mileage_lower), this, (dialog, which) -> callback.callback(true), (dialog, which) -> callback.callback(false));
            } else {
                callback.callback(true);
            }
        } catch (NumberFormatException e) {
            Toast.makeText(Startroute.this, getString(R.string.mileage_no_number), Toast.LENGTH_SHORT).show();
            callback.callback(false);
        }
    }

    public boolean checkActiveTrip() {
        if(((Global) this.getApplication()).getActiveTripManager().isActiveTrip()) {
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
        Trip currentTrip = ((Global) this.getApplication()).getActiveTripManager().getTrip();
        currentTrip.setTrackingSetting(0);

        // Show start city input
        findViewById(R.id.enterStartCityCont).setVisibility(View.VISIBLE);

        // Get mileage
        getLastMileage();
    }

    private abstract class ValidationCallback {
        public abstract void callback(boolean valid);
    }
}

