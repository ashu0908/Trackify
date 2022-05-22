package com.android.arijit.firebase.walker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FileUtils {

    private final static String TAG = FileUtils.class.getSimpleName();

    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";

    public static final String COVID_PREF = "covid_pref";
    public static final String MARKED_DATE = "MARKED_DATE";

    public static File createImageFile(final Context context) throws IOException {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalCacheDir();
        Log.i(TAG, "createImageFile: "+storageDir.getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",    /* suffix */
                storageDir      /* directory */
        );
    }

    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.apply();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF,
                Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }


    public static void saveDateToPreferenceCovidDate(Context context, LocalDate date) {
        SharedPreferences prefs = context.getSharedPreferences(COVID_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = prefs.edit();
        prefEditor.putString(MARKED_DATE, date.format(DateTimeFormatter.ISO_DATE));
        prefEditor.apply();
    }
    public static LocalDate getPreferenceCovidDate(Context context) {
        SharedPreferences myPrefs = context.getSharedPreferences(COVID_PREF,
                Context.MODE_PRIVATE);
        String dateString = myPrefs.getString(MARKED_DATE, null);
        return (dateString == null ? null : LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE));
    }

}
