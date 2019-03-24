package com.example.ninja.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityUtils {
    public static void changeActivity(Activity activity, Context packageContext, Class<?> cls) {
        Intent moveToHome = new Intent(packageContext, cls);
        activity.startActivity(moveToHome);
    }
}
