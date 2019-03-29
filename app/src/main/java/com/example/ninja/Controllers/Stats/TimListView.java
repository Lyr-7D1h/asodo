package com.example.ninja.Controllers.Stats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ninja.Controllers.MainActivity;
import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class TimListView extends BackButtonActivity {

    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tim_list_view);

        // Init ListView
        initListView();
    }

    public void initListView() {
        // Init
        final ListView tripsLV = (ListView) findViewById(R.id.tripsLV);

        // Create an ArrayAdapter from List
        arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1);

        // Set list view items
        setListViewItems();

        // DataBind ListView with items from ArrayAdapter
        tripsLV.setAdapter(arrayAdapter);

        // On item click
        tripsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(parent);
                Intent intent = new Intent(TimListView.this, TimListViewDetail.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    public void setListViewItems() {
        ((Global) this.getApplication()).getTripCache(new AsodoRequesterCallback() {
            @Override
            public void callback(JsonObject jsonResponse) {
                System.out.println(jsonResponse);
                JsonArray cachedTrips = new TripList(jsonResponse).getTrips();

                System.out.println(cachedTrips);
                // Add values
                for (int i = 0; i < cachedTrips.size(); i++) {
                    // Get trip
                    Trip trip = Trip.build(cachedTrips.get(i).getAsJsonObject());
                    System.out.println(trip);

                    // Set text
                    //TODO EXAMPLE - Doe hier wat je wilt
                    arrayAdapter.add(trip.getMileageStarted() + " - " + trip.getMileageEnded());

                    // Update ListView
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
