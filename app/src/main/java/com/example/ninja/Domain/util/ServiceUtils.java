package com.example.ninja.Domain.util;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.ninja.Domain.Global;

public class ServiceUtils {
    public static void killLocationService(Activity activity) {
        if(((Global) activity.getApplication()).getActiveTripManager().isActiveTrip()) {
            // Kill service
            killService(activity, ((Global) activity.getApplication()).getActiveTripManager().getLocationIntent());

            // Reset variables
            ((Global) activity.getApplication()).getActiveTripManager().setLocationIntent(null);
            ((Global) activity.getApplication()).getActiveTripManager().setActiveTrip(false);
        }
    }

    private static void killService(Activity activity, Intent intent) {
        if(intent != null) {
            activity.stopService(intent);
        }
    }
}
