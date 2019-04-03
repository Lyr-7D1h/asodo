package com.example.ninja.Controllers.Stats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class HistoryList extends BackButtonActivity {

    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitory_list);

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
                Intent intent = new Intent(HistoryList.this, HistoryListDetail.class);
                intent.putExtra("position", position);
                startActivity(intent);
            }
        });
    }

    public void setListViewItems() {
        ((Global) this.getApplication()).getTripCache(new AsodoRequesterCallback() {
            @Override
            public void callback(JsonObject jsonResponse) {
                JsonArray cachedTrips = new TripList(jsonResponse).getTrips();

                // Add values
                for (int i = cachedTrips.size() - 1; i >= 0; i--) {
                    // Get trip
                    Trip trip = Trip.build(cachedTrips.get(i).getAsJsonObject());

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
