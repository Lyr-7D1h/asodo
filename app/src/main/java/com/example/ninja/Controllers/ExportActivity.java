package com.example.ninja.Controllers;

import android.Manifest;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.Toast;

import com.example.ninja.Controllers.abstractActivities.BackButtonActivity;
import com.example.ninja.Domain.exporter.CustomCalendarViewListener;
import com.example.ninja.Domain.exporter.Exporter;
import com.example.ninja.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExportActivity extends BackButtonActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        // Request permission
        ActivityCompat.requestPermissions(ExportActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(ExportActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        // Customized Calendar
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.calendar);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);

        // Set month action bar
        getSupportActionBar().setTitle(""+new SimpleDateFormat("YYY MMMM").format(compactCalendarView.getFirstDayOfCurrentMonth().getTime()));

        // Set listener
        CustomCalendarViewListener listener = new CustomCalendarViewListener(this, ExportActivity.this, compactCalendarView);
        compactCalendarView.setListener(listener);

        // Settings spinner
        Spinner spinner = (Spinner) findViewById(R.id.selected);
        String[] items = new String[]{"Business Trip & Personal Trip", "Business Trip", "Personal Trip"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

        // On download button clicked
        Button download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener.getDates().length == 2) {
                    if (listener.getDates()[0] != null && listener.getDates()[1] != null) {
                        new Exporter(listener.getDates(), ExportActivity.this, spinner.getSelectedItem().toString());
                    } else {
                        Toast.makeText(ExportActivity.this, "You haven't selected 2 dates", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText((Activity) ExportActivity.this, "You haven't selected 2 dates", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Hide date buttons
        Button text = (Button) findViewById(R.id.date);
        text.setVisibility(View.GONE);
        Button text2 = (Button) findViewById(R.id.date2);
        text2.setVisibility(View.GONE);
    };
}