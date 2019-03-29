package com.example.ninja.Controllers.Stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class TimListViewDetail extends BackButtonActivity implements OnMapReadyCallback {

    private Trip detailTrip;
    private GoogleMap mMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_list_view_detail);

        // Init trip
        int position = getIntent().getIntExtra("position", -1);
        ((Global) this.getApplication()).getTripCache(new AsodoRequesterCallback() {
            @Override
            public void callback(JsonObject jsonResponse) {
                try {
                    // Set trip
                    JsonArray cachedTrips = new TripList(jsonResponse).getTrips();
                    detailTrip = Trip.build(cachedTrips.get(position).getAsJsonObject());

                    // Add details
                    addDetails();
                } catch (IndexOutOfBoundsException e) {
                    // Inform user
                    Toast.makeText(TimListViewDetail.this, "Fout bij het laden van route!", Toast.LENGTH_SHORT).show();

                    // Go back
                    finish();
                }
            }
        });

        // Init map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void addDetails() {
        //TODO EXAMPLE
        // Je kan ook de layout in de xml veranderen hoe je wil en gewoon invullen

        TextView detailsTV = findViewById(R.id.detailsTV);
        detailsTV.setText(String.valueOf(detailTrip.getMileageStarted() + " - " + detailTrip.getMileageEnded()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Init
        mMap = googleMap;

        //TODO EXAMPLE
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
