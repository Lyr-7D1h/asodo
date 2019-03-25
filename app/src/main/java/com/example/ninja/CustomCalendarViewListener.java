package com.example.ninja;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomCalendarViewListener {
    private Date firstDate = null;
    private Date secondDate = null;
    private CompactCalendarView calendar;
    private Activity activity;
    private View view;

    public CustomCalendarViewListener(Activity activity, Context context, CompactCalendarView calendar) {
        this.calendar = calendar;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.view = inflater.inflate(R.layout.activity_export, null);
        this.activity = activity;
        listener();
    }

    public void listener() {
        calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            Button text = (Button) view.findViewById(R.id.date);
            Button text2 = (Button) view.findViewById(R.id.date2);

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
                activity.getActionBar().setTitle(""+new SimpleDateFormat("YYY MMMM").format(firstDayOfNewMonth));
            }

            public void indicateInterval(Date start, Date end) {
                long current = start.getTime();
                if (current > end.getTime()) {
                    while (current > end.getTime()) {
                        Event ev = new Event(Color.parseColor("#ffb3b3"), current);
                        calendar.addEvent(ev);
                        current -= 1 * 24 * 60 * 60 * 1000;
                        System.out.println(current);
                    }
                } else if (current < end.getTime()) {
                    while (current < end.getTime()) {
                        Event ev = new Event(Color.parseColor("#ffb3b3"), current);
                        calendar.addEvent(ev);
                        current += 1 * 24 * 60 * 60 * 1000;
                    }
                }
            }
        });
    }
}
