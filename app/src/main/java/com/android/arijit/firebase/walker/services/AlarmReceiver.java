package com.android.arijit.firebase.walker.services;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    private static final String TAG = AlarmReceiver.class.getSimpleName();


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, DateReviewService.class);
        context.startService(i);

    }

    public static void scheduleAlarm(Context context) {
        Log.i(TAG, "scheduleAlarm: inside");
        Intent intent = new Intent(context, AlarmReceiver.class);

        @SuppressLint("UnspecifiedImmutableFlag")
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);


        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_DAY, pIntent);

    }

    public static void cancelAlarm(Context context) {
        Log.i(TAG, "cancelAlarm: inside");
        Intent intent = new Intent(context, AlarmReceiver.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }


}
