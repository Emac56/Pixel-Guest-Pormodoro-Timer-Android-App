package com.app.pixelguestpormodorotimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Fired by AlarmManager when a timer ends, even if the app is closed. */
public class AlarmReceiver extends BroadcastReceiver {
    static final String EXTRA_TITLE = "title";
    static final String EXTRA_BODY = "body";

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(EXTRA_TITLE);
        String body = intent.getStringExtra(EXTRA_BODY);
        Notifier.show(context,
                title != null ? title : "Pixel Quest",
                body != null ? body : "");
    }
}
