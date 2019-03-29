package com.example.ninja.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.ninja.Controllers.Routetracking.Route;
import com.example.ninja.Controllers.Routetracking.Startroute;
import com.example.ninja.Controllers.Stats.ItemListActivity;
import com.example.ninja.Controllers.Stats.TimListView;
import com.example.ninja.Controllers.loginscreen.LogActivity;
import com.example.ninja.Domain.Global;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.util.ActivityUtils;
import com.example.ninja.Domain.util.AlertUtils;
import com.example.ninja.Domain.util.CacheUtils;
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

        System.out.println(((Global) this.getApplication()).isActiveTrip());

        // Functionality of start button
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), Startroute.class);
                startActivity(intent);
            }
        });

        // Functionality of statistics button
        ImageButton statistics = (ImageButton) findViewById(R.id.statistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimListView.class);
                startActivity(intent);
            }
        });

        // Functionality of activity_export button
        Activity self = this;
        ImageButton export = (ImageButton) findViewById(R.id.export);
        export.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ActivityUtils.changeActivity(self, MainActivity.this, ExportActivity.class);
            }
        });

        // Functionality of options button
        ImageButton options = (ImageButton) findViewById(R.id.options);
        options.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("options"); // Placeholder

                CacheUtils.deleteCache(context, "trips.list");
                // Example Request
                String jsonString = "{\"username\":\"huts\",\"password\":\"huts\"}";
                JsonObject json = new JsonParser().parse(jsonString).getAsJsonObject();

                AsodoRequester.newRequest("authenticate", json, MainActivity.this, new CustomListener() {
                    @Override
                    public void onResponse(JsonObject jsonResponse) {
                        System.out.println(jsonResponse);
                    }
                });
            }
        });

        // Functionality of logout button
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Check for active trip
                if(((Global) self.getApplication()).isActiveTrip()) {
                    AlertUtils.showAlert("Doorgaan", "U heeft een actieve rit!\n\nAls u uitlogt zal deze rit verloren gaan.", self, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Kill service
                            ServiceUtils.killLocationService(self);

                            // Delete user data
                            CacheUtils.deleteCache(context, "user.cache");

                            // Move user to login
                            ActivityUtils.changeActivity(self, MainActivity.this, LogActivity.class);
                        }
                    }, true);
                } else {
                    // Delete user data
                    CacheUtils.deleteCache(context, "user.cache");

                    // Move user to login
                    ActivityUtils.changeActivity(self, MainActivity.this, LogActivity.class);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Reset start button text if active trip
        Button start = (Button) findViewById(R.id.start);
        if(((Global) this.getApplication()).isActiveTrip()) {
            start.setText(String.valueOf("Open actieve rit"));
        }
    }

    public void checkRedirectExtra() {
        if(getIntent().hasExtra("redirect")) {
            ActivityUtils.changeActivity(this, MainActivity.this, Route.class);
        }
    }
}
