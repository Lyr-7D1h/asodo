package com.example.ninja.Domain.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.ninja.R;

public class AlertUtils {

    public static void showAlert(String message, Activity context, DialogInterface.OnClickListener clickListener) {
        showAlert(context.getString(R.string.default_ok), message, context, clickListener);
    }

    public static void showAlert(String button, String message, Activity context, DialogInterface.OnClickListener clickListener) {
        showAlert(button, context.getString(R.string.nvt), message, context, clickListener, false);
    }

    public static void showAlert(String okButton, String cancelButton, String message, Activity context, DialogInterface.OnClickListener clickListener) {
        showAlert(okButton, cancelButton, message, context, clickListener, true);
    }

    private static void showAlert(String okButton, String cancelButton, String message, Activity context, DialogInterface.OnClickListener clickListener, boolean showNegative) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message).setCancelable(false).setPositiveButton(okButton, clickListener);
        if(showNegative) {
            builder.setNegativeButton(cancelButton, null);
        }
        builder.show();
    }
}