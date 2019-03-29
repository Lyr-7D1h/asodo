package com.example.ninja.Controllers.Routetracking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ninja.Domain.Global;
import com.example.ninja.Controllers.MainActivity;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.example.ninja.R;

public class Endroute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Trip currentTrip = ((Global) this.getApplication()).getTrip();
        final Context context = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_route);
        final TextView kmend = findViewById(R.id.kmend);

        kmend.setText(String.valueOf(currentTrip.getMileageStarted() + currentTrip.getEstimatedKMDriven()));

        final Activity self = this;
        final Button button = findViewById(R.id.checked);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                currentTrip.setMileageEnded(Integer.parseInt(kmend.getText().toString()));
                currentTrip.registerToDB(Endroute.this);

                // Cache
                TripList tripList = TripList.build(context);
                tripList.addTrip(currentTrip);
                tripList.cache(context);

                // Reset trip
                ((Global) self.getApplication()).setTrip(null);

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}

