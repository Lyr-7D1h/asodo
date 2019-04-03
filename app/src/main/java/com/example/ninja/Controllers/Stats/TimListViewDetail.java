package com.example.ninja.Controllers.Stats;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ninja.Controllers.Routetracking.Startroute;
import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Controllers.loginscreen.RegActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.maps.android.PolyUtil;

import java.util.List;

public class TimListViewDetail extends BackButtonActivity implements OnMapReadyCallback {

    private Trip detailTrip;
    private GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_list_view_detail);

        // Init trip
        OnMapReadyCallback self = this;
        int position = getIntent().getIntExtra("position", -1);
        ((Global) this.getApplication()).getTripCache(new AsodoRequesterCallback() {
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
                    Toast.makeText(TimListViewDetail.this, "Fout bij het laden van route!", Toast.LENGTH_SHORT).show();

                    // Go back
                    finish();
                }
            }
        });
    }

    public void addDetails() {
        //TODO EXAMPLE
        // Je kan ook de layout in de xml veranderen hoe je wil en gewoon invullen

        TextView detailsTV = findViewById(R.id.detailsTV);
        detailsTV.setText(String.valueOf("kilometers: " + detailTrip.getMileageStarted() + " - " + detailTrip.getMileageEnded()) +"\nVan-Naar: " + detailTrip.getCityStarted() + " - " + detailTrip.getCityEnded() + "\n\nBeschrijving: " + detailTrip.getDesDeviation());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Init

        if(detailTrip.getTrackingSetting() > 0) {

            mMap = googleMap;

            List<LatLng> points = PolyUtil.decode(detailTrip.getRoutePolyline()); // list of latlng

            for (int i = 0; i < points.size() - 1; i++) {
                LatLng src = points.get(i);
                LatLng dest = points.get(i + 1);

                // mMap is the Map Object
                Polyline line = mMap.addPolyline(
                        new PolylineOptions().add(
                                new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude)
                        ).width(5).color(Color.BLUE).geodesic(true)
                );
            }


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            mMap.setMaxZoomPreference(15);


            for (int i = 0; i < points.size(); i++) {
                builder.include(points.get(i));
            }
            LatLngBounds bounds = builder.build();
            int padding = 200; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);

        }else{
            findViewById(R.id.map).setVisibility(View.GONE);
        }
}
}
