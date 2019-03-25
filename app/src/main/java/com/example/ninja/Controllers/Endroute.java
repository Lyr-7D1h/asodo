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

public class Endroute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Trip currentTrip = (Trip)getIntent().getSerializableExtra("km");
        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.endcheck);
        final TextView kmend = findViewById(R.id.kmend);

        kmend.setText(String.valueOf(currentTrip.getVals().get("mileageEnded").getAsInt()));

        final Button button = findViewById(R.id.checked);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                currentTrip.setMileageEnded(Integer.parseInt(kmend.getText().toString()));
                currentTrip.builder(context);
                currentTrip.registerToDB(Endroute.this);

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

