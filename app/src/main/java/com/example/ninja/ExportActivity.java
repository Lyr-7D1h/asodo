package com.example.ninja;

import android.app.usage.UsageEvents;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

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

        new CustomCalendarViewListener(this,ExportActivity.this, compactCalendarView);
    };
}