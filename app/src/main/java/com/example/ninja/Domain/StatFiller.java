package com.example.ninja.Domain;


import com.example.ninja.Domain.trips.Trip;
import com.example.ninja.Domain.trips.TripList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class StatFiller {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<StatItem> ITEMS = new ArrayList<>();


    /**
     * A map of sample (dummy) items, by ID.
     */


    public static final Map<String, StatItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 10;



    static {


        for (int i = 0; i < COUNT; i++) {
            try {
                addItem(createStatItem(i));
            }
            catch(IndexOutOfBoundsException ex){
                //doe de huts
            }
        }
    }


    private static void addItem(StatItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static StatItem createStatItem(int position) {

        TripList tripList = TripList.build(Global.getContext());
        JsonArray trips = tripList.getTrips();

        Trip trip = Trip.build(trips.get(position).getAsJsonObject());
        JsonObject jtrip = trip.toJsonObject();


        return new StatItem(String.valueOf(position + 1), jtrip.get("mileageStarted").getAsString() + " - " + jtrip.get("mileageEnded").getAsString(), makeDetails(position));
    }


    private static String makeDetails(int position) {
        //subdata cache JSONS naar list [1 Json per entry], loop door List [i<count

        TripList tripList = TripList.build(Global.getContext());
        JsonArray trips = tripList.getTrips();
        Trip trip = Trip.build(trips.get(position).getAsJsonObject());
        JsonObject jtrip = trip.toJsonObject();

        StringBuilder builder = new StringBuilder();
        builder.append( "kilometers: \t" + jtrip.get("mileageStarted") + " - " + jtrip.get("mileageEnded") + "\n" +
                        "start/eindtijd: \t" + jtrip.get("tripStarted") + " - " + jtrip.get("tripEnded") + "\n\n\n" +
                jtrip.get("besAfwijking"));

        return builder.toString();
    }



    /**
     * A dummy item representing a piece of content.
     */


    public static class StatItem{
        public final String id;
        public final String content;
        public final String details;

        public StatItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }


        @Override
        public String toString() {
            return content;
        }


    }


}
