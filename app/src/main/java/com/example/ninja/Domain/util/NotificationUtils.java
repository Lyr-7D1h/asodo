package com.example.ninja.Domain.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.ninja.Controllers.MainActivity;
import com.example.ninja.Controllers.Routetracking.Route;
import com.example.ninja.R;

public class NotificationUtils {
    private static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getString(R.string.channel_name);
            String description = ctx.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Asodo", name, importance);
            channel.setVibrationPattern(new long[]{ 0 });
            channel.enableVibration(true);
            channel.setDescription(description);

            // Register channel
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static Notification buildNotification(Context ctx, NotificationCompat.Builder builder) {
        // Create notification channel
        createNotificationChannel(ctx);

        // Create Intent
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("redirect", 1);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        // Build notification
        builder = builder
                .setSmallIcon(R.drawable.transparentlogo)
                .setColor(0x2d4c2d)
                .setContentTitle(ctx.getString(R.string.notification_title))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        // Return notification
        return builder.build();
    }

    public static Notification buildNotification(Context ctx) {
        return buildNotification(ctx, new NotificationCompat.Builder(ctx, "Asodo"));
    }
}
