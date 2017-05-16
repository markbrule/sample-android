package com.example.mark.blueforcetracker;

import android.app.Application;
import android.content.Context;

/**
 * Created by mark on 5/15/17.
 */

public class BFTApplication extends Application {
    private static Context context;
    public static final String prefName = "BFTPreferences";

    public void onCreate() {
        super.onCreate();
        BFTApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BFTApplication.context;
    }
}

