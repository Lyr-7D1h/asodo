package com.example.ninja.Controllers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ninja.Domain.Trip;
import com.example.ninja.R;

public class Route extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Trip currentTrip = (Trip)getIntent().getSerializableExtra("km");
        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        final TextView kmtotaal = findViewById(R.id.kmtotaal);

        kmtotaal.setText(String.valueOf(currentTrip.getVals().get("startMileage").getAsInt()));

        final Button button = findViewById(R.id.endtrip);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                currentTrip.setEndMileage(Integer.parseInt(kmtotaal.getText().toString()));

                Intent intent = new Intent(v.getContext(), Endroute.class);
                intent.putExtra("km", currentTrip);
                startActivity(intent);
            }

        });
    }
}

