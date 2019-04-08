package com.example.ninja.Controllers.Routetracking;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.ninja.Controllers.LocationService;
import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Controllers.abstractActivities.PermissionActivity;
import com.example.ninja.Domain.coordinates.LatLngList;
import com.example.ninja.Domain.stateReceivers.LocationStateReceiver;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.util.AlertUtils;
import com.example.ninja.Domain.util.PermissionUtils;
import com.example.ninja.Domain.util.ServiceUtils;
import com.example.ninja.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Route extends BackButtonActivity implements LocationStateReceiver.LocationStateReceiverListener {

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
                switch (((Global) getApplication()).getTrip().getTrackingSetting()) {
                    case 2:
                        findViewById(R.id.kmtotaalCont).setVisibility(View.VISIBLE);
                    case 1:
                    case 0:
                        updateLayouts(startLoader, routeInformation);
                        break;
                }
            }

            // Estimation update
            if(intent.hasExtra("estimatedDistance")) {
                float estimatedDistance = intent.getFloatExtra("estimatedDistance", 0.f);
                updateEstimatedDistance(estimatedDistance);
            }

            // Final update
            if(intent.hasExtra("finalUpdate")) {
                lastUpdateReceived();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        System.out.println("FF create");

        // Init
        shownGpsPrompt = false;

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
        Trip currentTrip = ((Global) this.getApplication()).getTrip();

        // Set layout
        if(((Global) this.getApplication()).isActiveTrip()) {
            // Trip already active
            switch (((Global) this.getApplication()).getTripStatus()) {
                case 1:
                    this.startLoader.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    if(currentTrip.getTrackingSetting() == 2) {
                        findViewById(R.id.kmtotaalCont).setVisibility(View.VISIBLE);
                    }
                    this.routeLoader.setVisibility(View.VISIBLE);
                    requestUpdate();
                    break;
                case 3:
                    this.endLoader.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    lastUpdateReceived();
                    break;
                default:
                    lastUpdateReceived();
                    break;
            }
        } else {
            if(currentTrip.getTrackingSetting() == 0) {
                // Hide GPS prompts
                findViewById(R.id.gpsDisabledInfo1).setVisibility(View.GONE);
                findViewById(R.id.gpsDisabledInfo2).setVisibility(View.GONE);
                findViewById(R.id.gpsDisabledInfo3).setVisibility(View.GONE);
                shownGpsPrompt = true;
            }

            // Start new trip
            this.startLoader.setVisibility(View.VISIBLE);
            startLocationService();
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
        // Init currentTrip
        Trip currentTrip = ((Global) this.getApplication()).getTrip();

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
        ServiceUtils.killLocationService(this);
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

    public void lastUpdateReceived() {
        // Init
        Trip currentTrip = ((Global) getApplication()).getTrip();

        // Set trip ended
        currentTrip.setTripEnded();

        // Set polyline
        if (currentTrip.getTrackingSetting() > 0){
            currentTrip.setRoutePolyline(new LatLngList(currentTrip.getLocationList()).encode());
        }

        // Stop location service
        stopLocationService();

        // Move to next screen
        moveToRouteEnd();
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

    @Override
    public void locationAvailable() {
        int trackingSetting = ((Global) getApplication()).getTrip().getTrackingSetting();

        if(trackingSetting > 0) {
            findViewById(R.id.gpsDisabledInfo1).setVisibility(View.GONE);
            findViewById(R.id.gpsDisabledInfo2).setVisibility(View.GONE);

            if(trackingSetting == 2) {
                findViewById(R.id.gpsDisabledInfo3).setVisibility(View.GONE);
            }

            // Update status
            shownGpsPrompt = true;
        }
    }

    @Override
    public void locationUnavailable() {
        int trackingSetting = ((Global) getApplication()).getTrip().getTrackingSetting();

        if(trackingSetting > 0) {
            findViewById(R.id.enableLocationServices1).setOnClickListener(v -> showGpsPrompt());
            findViewById(R.id.enableLocationServices2).setOnClickListener(v -> showGpsPrompt());
            findViewById(R.id.gpsDisabledInfo1).setVisibility(View.VISIBLE);
            findViewById(R.id.gpsDisabledInfo2).setVisibility(View.VISIBLE);

            if(trackingSetting == 2) {
                findViewById(R.id.enableLocationServices3).setOnClickListener(v -> showGpsPrompt());
                findViewById(R.id.gpsDisabledInfo3).setVisibility(View.VISIBLE);
            }

            if (!shownGpsPrompt) {
                // Update status
                shownGpsPrompt = true;

                // Show alert
                AlertUtils.showAlert(getString(R.string.enable), getString(R.string.cancel), getString(R.string.gps_popup), this, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showGpsPrompt();
                    }
                });
            }
        }
    }

    public void showGpsPrompt() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }
}

