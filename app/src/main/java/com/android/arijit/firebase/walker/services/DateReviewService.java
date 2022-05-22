package com.android.arijit.firebase.walker.services;

import static com.android.arijit.firebase.walker.MainActivity.LAUNCH_FRAGMENT;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.android.arijit.firebase.walker.MainActivity;
import com.android.arijit.firebase.walker.R;
import com.android.arijit.firebase.walker.utils.FileUtils;
import com.android.arijit.firebase.walker.views.CovidFragment;

import java.time.LocalDate;
import java.time.Period;

public class DateReviewService extends IntentService {

    private static final int BASE_COVID_DURATION = 14;
    private static final String CHANNEL_ID = "Safety Review Channel";
    private static final int NOTIFICATION_ID = 10101;
    private static final String TAG = DateReviewService.class.getSimpleName();

    public DateReviewService() {
        super(DateReviewService.class.getName());
        Log.i(TAG, "DateReviewService: " +
                "constructor triggered");
    }

    private void createNotificationChannel() {
        NotificationChannel serviceChannel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.review_notification_channel),
                NotificationManager.IMPORTANCE_HIGH
        );
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);
    }

    private void createNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra(LAUNCH_FRAGMENT, CovidFragment.class.getName());
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setColor(ContextCompat.getColor(this, R.color.green_300))
                .setSmallIcon(R.drawable.ic_baseline_directions_24)
                .setContentTitle(getString(R.string.recovery_message_title))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.recovery_message)));

        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        manager.notify(NOTIFICATION_ID, mNotificationBuilder.build());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        LocalDate date = FileUtils.getPreferenceCovidDate(this);
        LocalDate today = LocalDate.now();
        Log.i(TAG, "onHandleIntent: into onHandleIntent DateReviewService " +
                date);
        Period period = Period.between(date, today);
        if(period.getYears() > 0 || period.getMonths() > 0 || period.getDays() > BASE_COVID_DURATION) {
            createNotificationChannel();
            createNotification();
        }


    }
}
