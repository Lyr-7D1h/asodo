package com.example.ninja.Domain.coordinates;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;

public class LatLngList {

    private List<LatLng> places;

    private LatLngList(List<LatLng> places) {
        this.places = places;
    }

    public LatLngList(LocationList locationList) {
        this.places = new ArrayList<>();
        initPlaces(locationList);
    }

    public LatLng get(int index) {
        return places.get(index);
    }

    public int size() {
        return places.size();
    }

    private void initPlaces(LocationList locationList) {
        ArrayList<Location> locList = locationList.getLocations();

        for(int i = 0; i < locList.size(); i++){
            places.add(new LatLng(locList.get(i).getLatitude(), locList.get(i).getLongitude()));
        }
    }

    public String encode() {
        return PolyUtil.encode(places);
    }

    public static LatLngList decode(String encodedPlaces) {
        return new LatLngList(PolyUtil.decode(encodedPlaces));
    }
}
