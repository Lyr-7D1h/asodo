package com.example.ninja.Controllers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ninja.Domain.Trip;
import com.example.ninja.R;

public class Startroute extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_route);

        final Button button = findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TextView content = findViewById(R.id.startkm); //gets you the contents of edit text
                String startkm = content.getText().toString();
                Trip currentTrip = new Trip(startkm);


                Intent intent = new Intent(v.getContext(), Route.class);
                intent.putExtra("km", currentTrip);
                startActivity(intent);
            }
        });



    }
}

