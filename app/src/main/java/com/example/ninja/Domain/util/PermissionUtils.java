package com.example.ninja.Domain.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class PermissionUtils {
    public static boolean hasPermission(Activity activity, String permission, int requestCode) {
        if (ActivityCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission}, requestCode);
            return false;
        }

        return true;
    }
}
