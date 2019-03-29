package com.example.ninja.Domain.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.ninja.Controllers.Route;
import com.example.ninja.R;

public class NotificationUtils {
    private static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = ctx.getString(R.string.channel_name);
            String description = ctx.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Asodo", name, importance);
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
        Intent intent = new Intent(ctx, Route.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, 0);

        // Build notification
        builder = builder.setSmallIcon(R.drawable.logo)
                .setContentTitle(ctx.getString(R.string.notification_title))
                .setContentText(ctx.getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        // Return notification
        return builder.build();
    }

    public static Notification buildNotification(Context ctx) {
        return buildNotification(ctx, new NotificationCompat.Builder(ctx, "Asodo"));
    }
}
