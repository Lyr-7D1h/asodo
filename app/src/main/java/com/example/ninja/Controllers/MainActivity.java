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
import android.widget.ImageButton;



import com.example.ninja.Controllers.loginscreen.LogActivity;
import com.example.ninja.Domain.httpRequests.CustomListener;
import com.example.ninja.Domain.httpRequests.AsodoRequester;
import com.example.ninja.Domain.util.ActivityUtils;
import com.example.ninja.Domain.util.CacheUtils;
import com.example.ninja.Settings.SettingsActivity;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.example.ninja.R;

public class MainActivity extends AppCompatActivity {



    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
                System.out.println("statistics"); // Placeholder
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
                // Delete user data
                CacheUtils.deleteCache(context, "user.cache");

                // Move user to login
                ActivityUtils.changeActivity(self, MainActivity.this, LogActivity.class);
            }
        });
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

