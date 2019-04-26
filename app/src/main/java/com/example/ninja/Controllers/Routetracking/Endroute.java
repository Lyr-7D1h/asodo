package com.example.ninja.Controllers.Routetracking;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.example.ninja.Domain.Global;
import com.example.ninja.Controllers.MainActivity;
import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.R;

public class Endroute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_route);

        // Init
        Trip currentTrip = ((Global) this.getApplication()).getActiveTripManager().getTrip();

        // Init number picker
        ((NumberPicker) findViewById(R.id.npEnd)).setWrapSelectorWheel(false);
        ((NumberPicker) findViewById(R.id.npEnd)).setMinValue(0);
        ((NumberPicker) findViewById(R.id.npEnd)).setMaxValue(1000000);

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
        Trip currentTrip = ((Global) this.getApplication()).getActiveTripManager().getTrip();
        ((NumberPicker) findViewById(R.id.npEnd)).setValue(currentTrip.getMileageStarted() + currentTrip.getEstimatedKMDriven());
    }

    public void onEndButtonClick(View v) {
        // Init
        Trip currentTrip = ((Global) this.getApplication()).getActiveTripManager().getTrip();

        // Update trip
        if(updateTrip()) {
            // Sync and cache trip
            ((Global) this.getApplication()).getSyncManager().addTripToCache(currentTrip);

            // Reset trip
            ((Global) this.getApplication()).getActiveTripManager().setTrip(null);

            // Move activity
            finish();
        }
    }

    public boolean updateTrip() {
        // Init
        Trip currentTrip = ((Global) this.getApplication()).getActiveTripManager().getTrip();

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
        String finalMileage = String.valueOf(((NumberPicker) findViewById(R.id.npEnd)).getValue());
        if(!validateMileage(finalMileage)) {
            return false;
        }

        currentTrip.setMileageEnded(Integer.parseInt(finalMileage));
        return true;
    }

    public boolean validateMileage(String finalMileage) {
        // Init
        Trip currentTrip = ((Global) this.getApplication()).getActiveTripManager().getTrip();

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

