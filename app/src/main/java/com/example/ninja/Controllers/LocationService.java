package com.example.ninja.Controllers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.coordinates.singleUpdates.SingleUpdateProvider;
import com.example.ninja.Domain.coordinates.singleUpdates.SingleUpdateReceiver;
import com.example.ninja.Domain.stateReceivers.LocationStateReceiver;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.coordinates.CustomLocationCallback;
import com.example.ninja.Domain.coordinates.CustomLocationRequest;
import com.example.ninja.Domain.util.NotificationUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationStateReceiver.LocationStateReceiverListener {

    private LocationService self = this;
    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private SingleUpdateProvider singleUpdateProvider;
    private LocationRequest locationRequest;
    private CustomLocationCallback locationCallback;
    private NotificationCompat.Builder notificationBuilder;
    private Trip currentTrip;
    private int trackingSetting = 0;
    private boolean isRequestingSingleUpdate = false;

    //BroadcastReceiver
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("finalUpdate")) {
                // Update status
                ((Global) getApplication()).updateTripStatus();

                if (trackingSetting > 0) {
                    // Stop tracking
                    if (trackingSetting == 2) {
                        stopTripTracking();
                    }

                    // Update status
                    isRequestingSingleUpdate = true;
                    ((Global) getApplication()).getLocationStateReceiver().requestUpdate(self);

                    // Request final location
                    singleUpdateProvider.requestSingleUpdate(new SingleUpdateReceiver() {
                        @Override
                        public void onLocation(Location location) {
                            // Update status
                            isRequestingSingleUpdate = false;
                            updateNotification("Bevestig kilometerstand");

                            // Broadcast update
                            broadcastFinalUpdate(location);
                        }
                    });
                } else {
                    broadcastFinalUpdate();
                }
            } else {
                broadcastEstimation();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // Create client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        // This registers mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mMessageReceiver,
                        new IntentFilter("routeBroadcaster"));

        // Listen to location changes
        ((Global) getApplication()).getLocationStateReceiver().addListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start foreground service
        notificationBuilder = new NotificationCompat.Builder(this, "Asodo");
        Notification notification = NotificationUtils.buildNotification(this, notificationBuilder);
        this.startForeground(1, notification);

        // Initiating GoogleApiClient connection
        googleApiClient.connect();

        // Init variables
        this.currentTrip = ((Global) this.getApplication()).getTrip();
        this.trackingSetting = intent.getIntExtra("trackingSetting", 0);

        // Init location variables
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        singleUpdateProvider = new SingleUpdateProvider(this, mFusedLocationClient);
        locationRequest = new CustomLocationRequest(1000, 5).getLocationRequest();
        locationCallback = new CustomLocationCallback(this, currentTrip);

        // Start service activities
        initService();

        // Super
        return super.onStartCommand(intent, flags, startId);
    }

    public void initService() {
        if(trackingSetting > 0) {
            // Update status
            isRequestingSingleUpdate = true;
            ((Global) getApplication()).getLocationStateReceiver().requestUpdate(self);

            // Request update
            singleUpdateProvider.requestSingleUpdate(new SingleUpdateReceiver() {
                @Override
                public void onLocation(Location location) {
                    // Update status
                    isRequestingSingleUpdate = false;
                    if(trackingSetting == 1) {
                        updateNotification("Beëindig rit");
                    } else {
                        updateNotification();
                    }

                    // Add location
                    currentTrip.addLocation(location);

                    // Update activity
                    broadcastNudes();

                    if(trackingSetting == 2) {
                        // Start tracking
                        startTripTracking();
                    }
                }
            });
        } else {
            updateNotification("Beëindig rit");
            broadcastNudes();
        }
    }

    public void requestSingleLocation(OnSuccessListener<Location> onSuccessListener) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        System.out.println("Getting single location!");
        mFusedLocationClient.getLastLocation().addOnSuccessListener(onSuccessListener);
    }

    public void startTripTracking() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        System.out.println("Getting locations!");
        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void stopTripTracking() {
        mFusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void updateNotification() {
        float estimation = currentTrip.getEstimatedKMDrivenf();
        updateNotification(String.valueOf("Afgelegde afstand: " + estimation + " km"));
    }

    public void updateNotification(String message) {
        if(notificationBuilder != null) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            notificationBuilder.setContentText(message);
            notificationManager.notify(1, notificationBuilder.build());
        }
    }

    public void broadcastNudes() {
        // Update status
        ((Global) getApplication()).updateTripStatus();

        // Send intent
        Intent intent = new Intent("locationBroadcaster");
        intent.putExtra("firstUpdate", 1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void broadcastEstimation() {
        Intent intent = new Intent("locationBroadcaster");
        intent.putExtra("estimatedDistance", currentTrip.getEstimatedKMDrivenf());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void broadcastFinalUpdate(Location location) {
        currentTrip.addLocation(location);
        broadcastFinalUpdate();
    }

    public void broadcastFinalUpdate() {
        // Update status
        ((Global) getApplication()).updateTripStatus();

        // Send intent
        Intent intent = new Intent("locationBroadcaster");
        intent.putExtra("finalUpdate", 1);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        System.out.println("Destroying!");
        super.onDestroy();

        // Stop listening to location changes
        ((Global) getApplication()).getLocationStateReceiver().removeListener(this);

        // Unregister BroadCastReceiver
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mMessageReceiver);

        // Stop location tracking
        mFusedLocationClient.removeLocationUpdates(locationCallback);

        // Close GoogleApiClient connection
        googleApiClient.disconnect();

        this.stopForeground(true);

        this.stopSelf();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Huts op!");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        System.out.println("Connected b*tches");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Connection suspended b*tches");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        System.out.println("Connection failed b*tches");
    }

    @Override
    public void locationAvailable() {
        if((trackingSetting == 1 && isRequestingSingleUpdate) || trackingSetting == 2) {
            int tripStatus = ((Global) getApplication()).getTripStatus();

            switch (tripStatus) {
                case 1:
                    updateNotification("Startlocatie wordt bepaald");
                    break;
                case 2:
                    updateNotification();
                    break;
                case 3:
                    updateNotification("Eindlocatie wordt bepaald");
                    break;
            }
        }
    }

    @Override
    public void locationUnavailable() {
        if((trackingSetting == 1 && isRequestingSingleUpdate) || trackingSetting == 2) {
            updateNotification("GPS Uitgeschakeld");
        }
    }
}
