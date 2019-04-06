package com.example.ninja.Domain.coordinates.singleUpdates;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.example.ninja.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LatLngWrapper {

    private LatLng latLng;

    public LatLngWrapper(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getCity(Context context) {
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> cityL = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            return cityL.get(0).getLocality();
        } catch (IOException e) {
            return context.getString(R.string.undefined);
        }
    }
}
