package com.app.pixelguestpormodorotimer;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.webkit.JavascriptInterface;

/**
 * Exposed to the web page as window.AndroidBridge.
 * The page calls these to schedule / cancel the native "timer finished" alarm.
 */
public class AndroidBridge {
    private static final int ALARM_REQUEST_CODE = 42;

    private final Activity activity;
    private final Context app;

    AndroidBridge(Activity activity) {
        this.activity = activity;
        this.app = activity.getApplicationContext();
    }

    private Intent alarmIntent() {
        return new Intent(app, AlarmReceiver.class);
    }

    /** Schedules (or replaces) the single alarm. atMillis is epoch milliseconds. */
    @JavascriptInterface
    public void scheduleAlarm(double atMillis, String title, String body) {
        long at = (long) atMillis;
        if (at <= System.currentTimeMillis()) return;
        AlarmManager am = app.getSystemService(AlarmManager.class);
        if (am == null) return;

        Intent intent = alarmIntent();
        intent.putExtra(AlarmReceiver.EXTRA_TITLE, title);
        intent.putExtra(AlarmReceiver.EXTRA_BODY, body);
        PendingIntent pi = PendingIntent.getBroadcast(app, ALARM_REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Exact alarms need no permission on Android 10; on 12+ fall back to inexact if not allowed.
        boolean exact = Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms();
        try {
            if (exact) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
            } else {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
            }
        } catch (SecurityException e) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi);
        }
    }

    @JavascriptInterface
    public void cancelAlarm() {
        AlarmManager am = app.getSystemService(AlarmManager.class);
        PendingIntent pi = PendingIntent.getBroadcast(app, ALARM_REQUEST_CODE, alarmIntent(),
                PendingIntent.FLAG_NO_CREATE | PendingIntent.FLAG_IMMUTABLE);
        if (pi != null) {
            if (am != null) am.cancel(pi);
            pi.cancel();
        }
    }

    /** Android 13+ only: asks for the notification permission. No-op on Android 10. */
    @JavascriptInterface
    public void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < 33) return;
        activity.runOnUiThread(() -> {
            if (activity.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        });
    }
}
