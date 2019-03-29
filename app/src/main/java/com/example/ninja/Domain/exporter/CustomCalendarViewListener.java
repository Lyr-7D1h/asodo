package com.example.ninja.Domain.exporter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.example.ninja.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomCalendarViewListener implements CompactCalendarView.CompactCalendarViewListener {
    private Date firstDate = null;
    private Date secondDate = null;
    private CompactCalendarView calendar;
    private AppCompatActivity activity;
    private View view;
    private Button text;
    private Button text2;

    public CustomCalendarViewListener(AppCompatActivity activity, Context context, CompactCalendarView calendar) {
        this.activity = activity;
        this.calendar = calendar;
        
        view = this.activity.getWindow().getDecorView().getRootView();

        this.text = (Button) view.findViewById(R.id.date);
        this.text2 = (Button) view.findViewById(R.id.date2);
    }

    @Override
    public void onDayClick(Date date) {
        if (firstDate == null) {
            firstDate = date;
            text.setText(new SimpleDateFormat("EEE d MMMM YYY").format(date));
            text.setVisibility(View.VISIBLE);
        } else if (secondDate == null) {
            secondDate = date;
            text2.setText(new SimpleDateFormat("EEE d MMMM YYY").format(date));
            text2.setVisibility(View.VISIBLE);
            indicateInterval(firstDate, secondDate);
        } else {
            calendar.removeAllEvents(); // Clear all events

            text.setVisibility(View.VISIBLE);
            text2.setVisibility(View.GONE);
            firstDate = date;
            secondDate = null;
            text.setText(new SimpleDateFormat("EEE d MMMM YYY").format(date));
        }

        Event ev = new Event(Color.parseColor("#ff6666"), date.getTime());

        calendar.addEvent(ev); // Reset Calendar
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        activity.getSupportActionBar().setTitle(""+new SimpleDateFormat("YYY MMMM").format(firstDayOfNewMonth));
    }

    private void indicateInterval(Date start, Date end) {
        long current = start.getTime();
        if (current > end.getTime()) {
            while (current > end.getTime()) {
                Event ev = new Event(Color.parseColor("#ffb3b3"), current);
                calendar.addEvent(ev);
                current -= 1 * 24 * 60 * 60 * 1000;
            }
        } else if (current < end.getTime()) {
            while (current < end.getTime()) {
                Event ev = new Event(Color.parseColor("#ffb3b3"), current);
                calendar.addEvent(ev);
                current += 1 * 24 * 60 * 60 * 1000;
            }
        }
    }
    public Date[] getDates() {
        Date[] dates = {this.firstDate, this.secondDate};
        return dates;
    }
}
