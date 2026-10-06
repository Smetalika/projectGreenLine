package com.example.project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceived extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String planName = intent.getStringExtra("planName");
        String dateTime = intent.getStringExtra("dateTime");

        if (planName != null && dateTime != null) {
            NowNotificstions.showNotification(context, planName, dateTime);
        }
        android.util.Log.d("NOTIFICATION", "onReceive вызван для " + planName);
    }
}
