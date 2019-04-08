package com.example.ninja.Controllers.Routetracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ninja.Domain.Global;
import com.example.ninja.Controllers.MainActivity;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.R;

public class Endroute extends AppCompatActivity {

    private EditText kmend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_route);

        // Init
        kmend = findViewById(R.id.kmend);
        Trip currentTrip = ((Global) this.getApplication()).getTrip();

        // Fill field
        fillEstimation();

        // Ask city
        if(currentTrip.getTrackingSetting() == 0) {
            findViewById(R.id.enterEndCityCont).setVisibility(View.VISIBLE);
        }

        // Checked button
        final Button button = findViewById(R.id.checked);
        button.setOnClickListener(this::onEndButtonClick);
    }

    public void fillEstimation() {
        Trip currentTrip = ((Global) this.getApplication()).getTrip();
        kmend.setText(String.valueOf(currentTrip.getMileageStarted() + currentTrip.getEstimatedKMDriven()));
    }

    public void onEndButtonClick(View v) {
        // Init
        Trip currentTrip = ((Global) this.getApplication()).getTrip();

        // Update trip
        if(updateTrip()) {
            // Sync and cache trip
            ((Global) this.getApplication()).addTripToCache(currentTrip);

            // Reset trip
            ((Global) this.getApplication()).setTrip(null);

            // Move activity
            finish();
        }
    }

    public boolean updateTrip() {
        // Init
        Trip currentTrip = ((Global) this.getApplication()).getTrip();

        // Get deviation
        currentTrip.setDesDeviation(((EditText) findViewById(R.id.desDeviation)).getText().toString());

        // Get end city
        if(currentTrip.getTrackingSetting() == 0) {
            String cityEntered = ((EditText) findViewById(R.id.enterEndCityET)).getText().toString();
            if(!cityEntered.isEmpty()) {
                currentTrip.setCityEnded(cityEntered);
            } else {
                Toast.makeText(Endroute.this, getString(R.string.enter_city), Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // Get final mileage
        String finalMileage = kmend.getText().toString();
        if(!validateMileage(finalMileage)) {
            return false;
        }

        currentTrip.setMileageEnded(Integer.parseInt(finalMileage));
        return true;
    }

    public boolean validateMileage(String finalMileage) {
        // Init
        Trip currentTrip = ((Global) this.getApplication()).getTrip();

        try {
            int res = Integer.parseInt(finalMileage);

            if(res < currentTrip.getMileageStarted()) {
                Toast.makeText(Endroute.this, getString(R.string.mileage_lower_than_start), Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(Endroute.this, getString(R.string.mileage_no_number), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}

