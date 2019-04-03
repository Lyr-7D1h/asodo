package com.example.ninja.Controllers.Routetracking;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.Controllers.LocationService;
import com.example.ninja.Domain.Global;
import com.example.ninja.Controllers.abstractActivities.PermissionActivity;
import com.example.ninja.Domain.stateReceivers.LocationStateReceiver;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.util.AlertUtils;
import com.example.ninja.Domain.util.PermissionUtils;
import com.example.ninja.R;

public class Route extends PermissionActivity implements LocationStateReceiver.LocationStateReceiverListener {

    private Trip currentTrip;
    private boolean shownGpsPrompt;

    // Layouts
    private ConstraintLayout routeInformation;
    private ConstraintLayout routeLoader;
    private ConstraintLayout startLoader;
    private ConstraintLayout endLoader;
    private ConstraintLayout cityFiller;

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
                        updateLayouts(startLoader, routeInformation);
                        break;
                    case 0:
                        askCityInput(startLoader);
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
                if(((Global) getApplication()).getTrip().getTrackingSetting() > 0) {
                    lastUpdateReceived();
                } else {
                    askCityInput(routeInformation);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
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
        this.cityFiller = findViewById(R.id.enterCity);
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
            System.out.println(((Global) this.getApplication()).getTripStatus());
            switch (((Global) this.getApplication()).getTripStatus()) {
                case 1:
                    this.startLoader.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    askCityInput(startLoader);
                    break;
                case 3:
                    if(currentTrip.getTrackingSetting() == 2) {
                        findViewById(R.id.kmtotaalCont).setVisibility(View.VISIBLE);
                    }
                    this.routeLoader.setVisibility(View.VISIBLE);
                    requestUpdate();
                    break;
                case 4:
                    this.endLoader.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    askCityInput(endLoader);
                    break;
                case 6:
                    lastUpdateReceived();
                    break;
            }
        } else {
            // Start new trip
            if(currentTrip.getTrackingSetting() > 0) {
                this.startLoader.setVisibility(View.VISIBLE);

                // Check location permission
                if(PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, this.get_REQUEST_CODE_FINE_LOCATION())) {
                    permissionAccepted(this.get_REQUEST_CODE_FINE_LOCATION());
                }
            } else {
                askCityInput(startLoader);
                permissionDeclined(get_REQUEST_CODE_FINE_LOCATION());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("FF Door");

        // Init currentTrip
        this.currentTrip = ((Global) this.getApplication()).getTrip();

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
        if(((Global) getApplication()).getTrip().getTrackingSetting() > 0) {
            updateLayouts(routeInformation, endLoader);
        } else {
            askCityInput(routeInformation);
        }

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

    public void askCityInput(ConstraintLayout hide) {
        // Update layouts
        updateLayouts(hide, cityFiller);
        if(((Global) getApplication()).getTrip().getCityStarted() != null) {
            ((EditText) findViewById(R.id.enterCityET)).setText(null);
            findViewById(R.id.enterStartCityTV).setVisibility(View.GONE);
            findViewById(R.id.enterEndCityTV).setVisibility(View.VISIBLE);
        }

        // Set on click listener
        ((Button) findViewById(R.id.enterCitySubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityEntered = ((EditText) findViewById(R.id.enterCityET)).getText().toString();
                if(!cityEntered.isEmpty()) {
                    // Update status
                    ((Global) getApplication()).updateTripStatus();

                    // Set city
                    currentTrip = ((Global) getApplication()).getTrip();
                    if (currentTrip.getCityStarted() == null) {
                        currentTrip.setCityStarted(cityEntered);
                        updateLayouts(cityFiller, routeInformation);
                        requestUpdate();
                    } else {
                        currentTrip.setCityEnded(cityEntered);
                        lastUpdateReceived();
                    }
                } else {
                    Toast.makeText(Route.this, "Vul een stad in", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void lastUpdateReceived() {
        // Update current Trip
        currentTrip = ((Global) getApplication()).getTrip();
        // TODO calculate polyline, cityStarted, cityEnded, optimalDistance, kmDeviation
        currentTrip.setTripEnded();

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
        this.currentTrip = ((Global) this.getApplication()).getTrip();
        currentTrip.setTrackingSetting(0);

        // Hide GPS prompts
        findViewById(R.id.gpsDisabledInfo1).setVisibility(View.GONE);
        findViewById(R.id.gpsDisabledInfo2).setVisibility(View.GONE);
        findViewById(R.id.gpsDisabledInfo3).setVisibility(View.GONE);
        shownGpsPrompt = true;

        // Start service
        startLocationService();
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
                AlertUtils.showAlert("Inschakelen", "Annuleren", "GPS uitgeschakeld.\nKan locatie niet bepalen.", this, new DialogInterface.OnClickListener() {
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

