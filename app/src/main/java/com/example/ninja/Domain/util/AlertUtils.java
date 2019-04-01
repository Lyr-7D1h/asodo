package com.example.ninja.Domain.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AlertUtils {

    public static void showAlert(String message, Activity context, DialogInterface.OnClickListener clickListener) {
        showAlert("OK", message, context, clickListener);
    }

    public static void showAlert(String button, String message, Activity context, DialogInterface.OnClickListener clickListener) {
        showAlert(button, message, context, clickListener, false);
    }

    public static void showAlert(String button, String message, Activity context, DialogInterface.OnClickListener clickListener, boolean showNegative) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message).setCancelable(false).setPositiveButton(button, clickListener);
        if(showNegative) {
            builder.setNegativeButton("Cancel", null);
        }
        builder.show();
    }
}