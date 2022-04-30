package com.android.arijit.firebase.walker.utils;

import android.app.Activity;

import androidx.activity.OnBackPressedCallback;

import com.android.arijit.firebase.walker.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class OnBackPressImpl extends OnBackPressedCallback {
    private final Activity activity;
    private OnBackPressImpl(Activity activity) {
        super(true);
        this.activity = activity;
    }
    @Override
    public void handleOnBackPressed() {
        ((BottomNavigationView) activity.findViewById(R.id.navigation))
                .setSelectedItemId(R.id.navigation_home);
    }

    public static OnBackPressedCallback getInstance(Activity activity) {
        return new OnBackPressImpl(activity);
    }

}
