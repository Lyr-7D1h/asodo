package com.example.ninja.Controllers.Stats;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.coordinates.LatLngList;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.Domain.util.ConnectivityUtils;
import com.example.ninja.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HistoryListDetail extends BackButtonActivity implements OnMapReadyCallback {

    private Trip detailTrip;
    private GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list_detail);

//        ((ConstraintLayout) findViewById(R.id.mapCont));

        // Init trip
        OnMapReadyCallback self = this;
        int position = getIntent().getIntExtra("position", -1);
        ((Global) this.getApplication()).getSyncManager().getTripCache(new AsodoRequesterCallback() {
            @Override
            public void callback(JsonObject jsonResponse) {
                try {
                    // Set trip
                    JsonArray cachedTrips = new TripList(jsonResponse).getTrips();
                    detailTrip = Trip.build(cachedTrips.get(cachedTrips.size() - (position + 1)).getAsJsonObject());

                    // Add details
                    addDetails();

                    // Init map
                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(self);
                } catch (IndexOutOfBoundsException e) {
                    // Inform user
                    Toast.makeText(HistoryListDetail.this, getString(R.string.activity_history_list_detail_invalid_detail), Toast.LENGTH_SHORT).show();

                    // Go back
                    finish();
                }
            }
        });
    }

    public void addDetails() {
        // Set city started
        String cityStarted = detailTrip.getCityStarted();
        if(cityStarted.isEmpty()) {
            cityStarted = getString(R.string.undefined);
        }
        ((TextView) findViewById(R.id.detailsCityStartedTV)).setText(cityStarted);

        // Set city ended
        String cityEnded = detailTrip.getCityEnded();
        if(cityEnded.isEmpty()) {
            cityEnded = getString(R.string.undefined);
        }
        ((TextView) findViewById(R.id.detailsCityEndedTV)).setText(cityEnded);

        // Init dateformats
        DateFormat dateFormatFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        DateFormat dateFormatDate = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        DateFormat dateFormatTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

        // Set date
        String tripEnded = detailTrip.getTripEnded();
        String dateEnded;
        try {
            dateEnded = dateFormatDate.format(dateFormatFrom.parse(tripEnded));
        } catch (ParseException e) {
            dateEnded = getString(R.string.undefined);
        }
        ((TextView) findViewById(R.id.detailsDateTV)).setText(dateEnded);

        // Set time started
        String tripStarted = detailTrip.getTripStarted();
        String timeStarted;
        try {
            timeStarted = dateFormatTime.format(dateFormatFrom.parse(tripStarted));
        } catch (ParseException e) {
            timeStarted = getString(R.string.undefined);
        }
        ((TextView) findViewById(R.id.detailsTimeStartedTV)).setText(timeStarted);

        // Set time ended
        String timeEnded;
        try {
            timeEnded = dateFormatTime.format(dateFormatFrom.parse(tripEnded));
        } catch (ParseException e) {
            timeEnded = getString(R.string.undefined);
        }
        ((TextView) findViewById(R.id.detailsTimeEndedTV)).setText(timeEnded);

        // Set trip duration
        long tripDuration = detailTrip.getTripDuration();
        int seconds = (int) (tripDuration / 1000) % 60 ;
        int minutes = (int) ((tripDuration / (1000*60)) % 60);
        int hours   = (int) ((tripDuration / (1000*60*60)) % 24);
        ((TextView) findViewById(R.id.detailsTripDurationTV)).setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds));


        // Set mileages
        ((TextView) findViewById(R.id.detailsMileageStartedTV)).setText(String.valueOf(detailTrip.getMileageStarted()));
        ((TextView) findViewById(R.id.detailsMileageEndedTV)).setText(String.valueOf(detailTrip.getMileageEnded()));
        ((TextView) findViewById(R.id.detailsDistanceDrivenTV)).setText(String.valueOf(detailTrip.getDistanceDriven()));

        // Set businesstrip
        int businessTrip = detailTrip.getBusinessTrip();
        String businessString = getString(R.string.activity_history_list_detail_label_no);
        if(businessTrip == 1) {
            businessString = getString(R.string.activity_history_list_detail_label_yes);
        }
        ((TextView) findViewById(R.id.detailsBusinessTripTV)).setText(businessString);

        // Set bbCommuting
        if(businessTrip == 0) {
            findViewById(R.id.detailsBbCommutingCont).setVisibility(View.GONE);
        } else {
            int bbCommuting = detailTrip.getBbCommuting();
            System.out.println(bbCommuting);
            String bbString = getString(R.string.activity_history_list_detail_label_yes);
            if(bbCommuting == 1) {
                bbString = getString(R.string.activity_history_list_detail_label_no);
            }
            ((TextView) findViewById(R.id.detailsBbCommutingTV)).setText(bbString);
        }

        // Set desDeviation
        String desDeviation = detailTrip.getDesDeviation();
        if(desDeviation.isEmpty()) {
            findViewById(R.id.detailsDesDeviationCont).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.detailsDesDeviationTV)).setText(desDeviation);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(detailTrip.getTrackingSetting() > 0 && (!detailTrip.getRoutePolyline().isEmpty() && detailTrip.getRoutePolyline() != null) && ConnectivityUtils.isNetworkAvailable(this)) {
            // Init
            mMap = googleMap;
            mMap.setMaxZoomPreference(17);
            LatLngList places = LatLngList.decode(detailTrip.getRoutePolyline()); // list of latlng

            // Create markers
            mMap.addMarker(new MarkerOptions().position(places.get(0)).title(getString(R.string.start)).zIndex(1.0f));
            mMap.addMarker(new MarkerOptions().position(places.get(places.size()-1)).title(getString(R.string.end)));

            // Create polyline
            if(detailTrip.getTrackingSetting() == 2) {
                for (int i = 0; i < places.size() - 1; i++) {
                    LatLng src = places.get(i);
                    LatLng dest = places.get(i + 1);

                    // mMap is the Map Object
                    mMap.addPolyline(
                            new PolylineOptions().add(
                                    new LatLng(src.latitude, src.longitude),
                                    new LatLng(dest.latitude, dest.longitude)
                            ).width(5).color(Color.BLUE).geodesic(true)
                    );
                }
            }

            // Generate bounds
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (int i = 0; i < places.size(); i++) {
                builder.include(places.get(i));
            }
            LatLngBounds bounds = builder.build();

            // Create camera
            int padding = 100; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        } else {
            findViewById(R.id.mapCont).setVisibility(View.GONE);
        }
    }
}
