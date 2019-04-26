package com.example.ninja.Controllers.Stats;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Controllers.loginscreen.RegActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.AsodoRequesterCallback;
import com.example.ninja.Domain.stateReceivers.NetworkStateReceiver;
import com.example.ninja.Domain.stateReceivers.SyncStateReceiver;
import com.example.ninja.Domain.stats.CustomArrayAdapter;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HistoryList extends BackButtonActivity implements SyncStateReceiver.SyncStateReceiverListener {

    private CustomArrayAdapter arrayAdapter;
    private SwipeRefreshLayout historyListRefresh;
    private boolean hasBeenLoaded;
    private boolean showingCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hitory_list);

        // Init
        hasBeenLoaded = false;
        showingCache = true;

        // Init ListView
        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        System.out.println(hasBeenLoaded);

        // Register for network updates
        ((Global) getApplication()).getSyncStateReceiver().addListener(this);
    }

    public void initListView() {
        // Init
        final ListView tripsLV = (ListView) findViewById(R.id.tripsLV);

        // On refresh
        historyListRefresh = findViewById(R.id.historyListRefresh);
        historyListRefresh.setOnRefreshListener(() -> setListViewItems(false));

        // Create an ArrayAdapter from List
        arrayAdapter = new CustomArrayAdapter
                (this, android.R.layout.simple_list_item_2, android.R.id.text1);

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

    public void setListViewItems(boolean refresh) {
        // Update refresher status
        historyListRefresh.setRefreshing(refresh);

        ((Global) this.getApplication()).getSyncManager().getTripCache(new AsodoRequesterCallback() {
            @Override
            public void callback(JsonObject jsonResponse) {
                JsonArray cachedTrips = new TripList(jsonResponse).getTrips();

                // Clear listview
                arrayAdapter.clear();
                arrayAdapter.notifyDataSetChanged();

                // Add values
                for (int i = cachedTrips.size() - 1; i >= 0; i--) {
                    // Get trip
                    Trip trip = Trip.build(cachedTrips.get(i).getAsJsonObject());

                    // Prepare variables
                    String cityStarted = trip.getCityStarted();
                    if(cityStarted.isEmpty()) {
                        cityStarted = getString(R.string.undefined);
                    }

                    String cityEnded = trip.getCityEnded();
                    if(cityEnded.isEmpty()) {
                        cityEnded = getString(R.string.undefined);
                    }

                    DateFormat dateFormatFrom = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    DateFormat dateFormatTo = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
                    String date = null;
                    try {
                        Date temp = dateFormatFrom.parse(trip.getTripEnded());
                        date = dateFormatTo.format(temp);
                    } catch (ParseException e) {
                        date = getString(R.string.undefined);
                    }

                    // Set text
                    String[] item = new String[2];
                    item[0] = String.format(getString(R.string.activity_history_list_title), date, cityStarted, cityEnded);
                    item[1] = String.format(getString(R.string.activity_history_list_description), trip.getDistanceDriven());
                    arrayAdapter.add(item);

                    // Update ListView
                    arrayAdapter.notifyDataSetChanged();
                }

                // Update refresher status
                historyListRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister for network updates
        ((Global) getApplication()).getSyncStateReceiver().removeListener(this);
    }

    @Override
    public void onSync() {
        if(showingCache || !hasBeenLoaded) {
            // Update status
            showingCache = false;
            setListViewItems(true);

            // Show message to user
            if(hasBeenLoaded) {
                Toast.makeText(HistoryList.this, getString(R.string.activity_history_list_refres_results), Toast.LENGTH_SHORT).show();
            }
        }

        hasBeenLoaded = true;
    }

    @Override
    public void onDesync() {
        if(!showingCache || !hasBeenLoaded) {
            // Update list
            showingCache = true;
            setListViewItems(false);

            // Show message to user
            Toast.makeText(HistoryList.this, getString(R.string.activity_history_list_show_cache), Toast.LENGTH_SHORT).show();
        }

        hasBeenLoaded = true;
    }
}
