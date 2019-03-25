package com.example.ninja.Controllers;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ninja.Domain.exporter.CustomCalendarViewListener;
import com.example.ninja.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ExportActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        Button download = (Button) findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("download"); // Placeholder
            }
        });

        Button text = (Button) findViewById(R.id.date);
        text.setVisibility(View.GONE);
        Button text2 = (Button) findViewById(R.id.date2);
        text2.setVisibility(View.GONE);

        // Customized Calendar
        final CompactCalendarView compactCalendarView = (CompactCalendarView) findViewById(R.id.calendar);
        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setUseThreeLetterAbbreviation(true);
        compactCalendarView.shouldSelectFirstDayOfMonthOnScroll(false);

        // Set month action bar
        getSupportActionBar().setTitle(""+new SimpleDateFormat("YYY MMMM").format(compactCalendarView.getFirstDayOfCurrentMonth().getTime()));

        // Set listener
        compactCalendarView.setListener(new CustomCalendarViewListener(this, ExportActivity.this, compactCalendarView));
    };
}