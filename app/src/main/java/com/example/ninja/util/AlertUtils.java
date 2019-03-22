package com.example.ninja.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AlertUtils {

    public static void showAlert(String message, Activity context) {
        showAlert("OK", message, context);
    }

    public static void showAlert(String button, String message, Activity context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message).setCancelable(false)
                .setPositiveButton(button, (dialog, id) -> {
                    // TODO
                });

        try {
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}