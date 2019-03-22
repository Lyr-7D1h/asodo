package com.example.ninja;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ninja.Domain.Trip;

public class Route extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Trip currentTrip = (Trip)getIntent().getSerializableExtra("startkm");
        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.route);
        TextView kmtotaal = findViewById(R.id.kmtotaal);

        kmtotaal.setText(currentTrip.getVals()[0]);

        final Button button = findViewById(R.id.endtrip);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TextView kmtotaal = findViewById(R.id.kmtotaal);

                currentTrip.setEnd(kmtotaal.getText().toString());

                currentTrip.builder(context);
            }

    });
}
}

