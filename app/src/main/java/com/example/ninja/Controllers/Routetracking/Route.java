package com.example.ninja.Controllers.Routetracking;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ninja.Controllers.LocationService;
import com.example.ninja.Domain.Global;
import com.example.ninja.Controllers.abstractActivities.PermissionActivity;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.network.LocationStateReceiver;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.util.AlertUtils;
import com.example.ninja.Domain.util.PermissionUtils;
import com.example.ninja.R;
import com.google.gson.JsonObject;

public class Route extends PermissionActivity implements LocationStateReceiver.LocationStateReceiverListener {

    private Trip currentTrip;
    private boolean shownGpsPrompt;

    // Layouts
    private ConstraintLayout routeInformation;
    private ConstraintLayout routeLoader;
    private ConstraintLayout startLoader;
    private ConstraintLayout endLoader;

    // BroadcastReceiver
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // First location receiced
            if(intent.hasExtra("firstUpdate")) {
                // Update layouts
                updateLayouts(startLoader, routeInformation);
            }

            // Estimation update
            if(intent.hasExtra("estimatedDistance")) {
                float estimatedDistance = intent.getFloatExtra("estimatedDistance", 0.f);
                updateEstimatedDistance(estimatedDistance);
            }

            // Final update
            if(intent.hasExtra("finalUpdate")) {
                // Update current Trip
                // TODO calculate polyline, cityStarted, cityEnded, optimalDistance, kmDeviation
                currentTrip.setTripEnded();

                // Stop location service
                stopLocationService();

                // Move to next screen
                moveToRouteEnd();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);

        // Init
        this.shownGpsPrompt = false;

        // Init layouts
        initLayouts();

        // Set button listener
        Activity self = this;
        final Button button = findViewById(R.id.endtrip);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                requestFinalUpdate();
            }
        });
    }

    private void initLayouts() {
        this.routeInformation = findViewById(R.id.routeInformation);
        this.routeLoader = findViewById(R.id.routeLoader);
        this.startLoader = findViewById(R.id.startLoader);
        this.endLoader = findViewById(R.id.endLoader);
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("FF Start");

        // Init currentTrip
        this.currentTrip = ((Global) this.getApplication()).getTrip();

        // Set layout
        if(((Global) this.getApplication()).isActiveTrip()) {
            // Trip already active
            this.routeLoader.setVisibility(View.VISIBLE);
            requestUpdate();
        } else {
            // Start new trip
            this.startLoader.setVisibility(View.VISIBLE);

            // Check location permission
            if(PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, this.get_REQUEST_CODE_FINE_LOCATION())) {
                permissionAccepted(this.get_REQUEST_CODE_FINE_LOCATION());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("FF Door");

        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("locationBroadcaster"));
        requestUpdate();

        // Listen to location changes
        ((Global) getApplication()).getLocationStateReceiver().addListener(this);
    }

    public void startLocationService() {
        // Get tracking setting
        int trackingSetting = currentTrip.getTrackingSetting();

        // Start up service
        Intent locationIntent = new Intent(Route.this, LocationService.class);
        locationIntent.putExtra("trackingSetting", trackingSetting);
        ((Global) this.getApplication()).setLocationIntent(locationIntent);
        startService(locationIntent);

        // Set trip status to active
        ((Global) this.getApplication()).setActiveTrip(true);
    }

    public void requestUpdate() {
        Intent intent = new Intent("routeBroadcaster");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        System.out.println("request update");
    }

    public void requestFinalUpdate() {
        // Update layouts
        updateLayouts(routeInformation, endLoader);

        // Send intent
        Intent intent = new Intent("routeBroadcaster");
        intent.putExtra("finalUpdate", 1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void stopLocationService() {
        // Stop location service
        stopService(((Global) this.getApplication()).getLocationIntent());
        ((Global) this.getApplication()).setLocationIntent(null);

        // Set trip status to inactive
        ((Global) this.getApplication()).setActiveTrip(false);
    }

    public void updateLayouts(ConstraintLayout hide, ConstraintLayout show) {
        hide.setVisibility(View.GONE);
        show.setVisibility(View.VISIBLE);
    }

    public void updateEstimatedDistance(float estimation) {
        // Update layouts
        if(this.routeLoader.getVisibility() == View.VISIBLE) {
            updateLayouts(routeLoader, routeInformation);
        }

        // Update TextView
        final TextView kmend = findViewById(R.id.kmtotaal);
        kmend.setText(String.valueOf(estimation));
    }

    public void moveToRouteEnd() {
        // Move to next page
        Intent intent = new Intent(Route.this, Endroute.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);

        // Stop listening to location changes
        ((Global) getApplication()).getLocationStateReceiver().removeListener(this);

        System.out.println("FF Pauze");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // Location permission accepted
    @Override
    public void permissionAccepted(int requestCode) {
        // Start service
        startLocationService();
    }

    // Location permission declined
    @Override
    public void permissionDeclined(int requestCode) {
        // Change tracking setting
        //TODO to 0
        currentTrip.setTrackingSetting(0);

        // Start service
        startLocationService();
    }

    @Override
    public void locationAvailable() {
        findViewById(R.id.gpsDisabledInfo).setVisibility(View.GONE);

        // Update status
        shownGpsPrompt = true;
    }

    @Override
    public void locationUnavailable() {
        findViewById(R.id.enableLocationServices).setOnClickListener(v -> showGpsPrompt());
        findViewById(R.id.gpsDisabledInfo).setVisibility(View.VISIBLE);

        if(!shownGpsPrompt) {
            // Update status
            shownGpsPrompt = true;

            // Show alert
            AlertUtils.showAlert("Inschakelen", "Annuleren", "GPS uitgeschakeld.\nKan locatie niet bepalen.", this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showGpsPrompt();
                }
            });
        }
    }

    public void showGpsPrompt() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
}

