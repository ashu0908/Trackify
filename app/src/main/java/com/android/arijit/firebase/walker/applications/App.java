package com.android.arijit.firebase.walker.applications;

import static com.android.arijit.firebase.walker.MainActivity.THEME_KEY;
import static com.android.arijit.firebase.walker.MainActivity.UNIT_KEY;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.android.arijit.firebase.walker.views.SettingsFragment;

public class App extends Application {
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        initSettings();
    }

    public static Context getContext(){
        return mContext;
    }

    private void initSettings(){
        SharedPreferences sh = getSharedPreferences(SettingsFragment.SH, Context.MODE_PRIVATE);
        SettingsFragment.SYSTEM_THEME = sh.getInt(THEME_KEY, 0);
        SettingsFragment.SYSTEM_UNIT = sh.getInt(UNIT_KEY, 0);

        int nightMode;
        switch (SettingsFragment.SYSTEM_THEME){
            case 1:
                nightMode = AppCompatDelegate.MODE_NIGHT_NO;
                break;
            case 2:
                nightMode = AppCompatDelegate.MODE_NIGHT_YES;
                break;
            default:
                nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
        }
        AppCompatDelegate.setDefaultNightMode(nightMode);

    }

}
