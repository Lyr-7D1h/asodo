package com.example.ninja.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.ninja.Controllers.Routetracking.Route;
import com.example.ninja.Controllers.Routetracking.Startroute;
import com.example.ninja.Controllers.Stats.HistoryList;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.util.ActivityUtils;
import com.example.ninja.Domain.util.AlertUtils;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Settings.SettingsActivity;
import com.example.ninja.Domain.util.ServiceUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.example.ninja.R;

public class MainActivity extends AppCompatActivity {

    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkRedirectExtra();

        // Functionality of start button
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Startroute.class);
                startActivity(intent);
            }
        });

        // Functionality of statistics button
        Button statistics = (Button) findViewById(R.id.statistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryList.class);
                startActivity(intent);
            }
        });

        // Functionality of activity_export button
        Activity self = this;
        Button export = (Button) findViewById(R.id.export);
        export.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActivityUtils.changeActivity(self, MainActivity.this, ExportActivity.class);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Reset start button text if active trip
        Button start = (Button) findViewById(R.id.start);
        if(((Global) this.getApplication()).isActiveTrip()) {
            start.setText(String.valueOf(getString(R.string.activity_main_active_trip)));
        }
    }

    public void checkRedirectExtra() {
        if(getIntent().hasExtra("redirect")) {
            ActivityUtils.changeActivity(this, MainActivity.this, Route.class);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_items,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;

                default:return super.onOptionsItemSelected(item);
        }
    }
}
