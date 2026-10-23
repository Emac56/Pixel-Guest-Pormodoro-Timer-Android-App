package com.app.pixelguestpormodorotimer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

/** Creates the notification channel and shows the "timer finished" notification. */
final class Notifier {
    static final String CHANNEL_ID = "pixel_quest_timer";
    private static final int NOTIFICATION_ID = 1001;

    private Notifier() {}

    static void ensureChannel(Context ctx) {
        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (nm == null || nm.getNotificationChannel(CHANNEL_ID) != null) return;
        NotificationChannel ch = new NotificationChannel(
                CHANNEL_ID, "Quest timer alerts", NotificationManager.IMPORTANCE_HIGH);
        ch.setDescription("Tells you when a focus, reward or break timer ends");
        ch.enableVibration(true);
        ch.setVibrationPattern(new long[]{0, 200, 100, 200});
        nm.createNotificationChannel(ch);
    }

    static void show(Context ctx, String title, String body) {
        // Android 13+: without the permission the notification would be dropped anyway.
        if (Build.VERSION.SDK_INT >= 33
                && ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (nm == null) return;
        ensureChannel(ctx);

        Intent open = new Intent(ctx, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                ctx, 0, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification n = new Notification.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_stat_quest)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new Notification.BigTextStyle().bigText(body))
                .setCategory(Notification.CATEGORY_REMINDER)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();
        nm.notify(NOTIFICATION_ID, n);
    }
}
