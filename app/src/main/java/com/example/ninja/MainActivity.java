package com.example.ninja;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.ninja.httpRequests.CustomListener;
import com.example.ninja.httpRequests.AsodoRequester;
import com.example.ninja.util.AlertUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Functionality of start button
        Button start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("start"); // Placeholder
            }
        });
        // Functionality of statistics button
        ImageButton statistics = (ImageButton) findViewById(R.id.statistics);
        statistics.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("statistics"); // Placeholder
            }

        });
        // Functionality of export button
        ImageButton export = (ImageButton) findViewById(R.id.export);
        export.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("export"); // Placeholder
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

    }
}
