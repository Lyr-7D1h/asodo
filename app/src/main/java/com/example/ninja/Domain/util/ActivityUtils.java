package com.example.ninja.Domain.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class ActivityUtils {
    /**
     * Helper class to move to a different activity
     *
     * @param activity
     * @param packageContext
     * @param cls
     */
    public static void changeActivity(Activity activity, Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        activity.startActivity(intent);
    }
}
