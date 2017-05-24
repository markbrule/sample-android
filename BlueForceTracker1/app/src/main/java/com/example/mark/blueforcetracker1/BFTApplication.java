package com.example.mark.blueforcetracker1;

import android.app.Application;
import android.content.Context;

/**
 * Created by mark on 5/22/17.
 */

public class BFTApplication extends Application {
    private static Context context;
    public static final String prefName = "BFTPreferences";
    public static final String serverKey = "BFTA.server";
    public static final String portNumber = "BTFA.port";
    public static final String queueName = "BTFA.queue";

    @Override
    public void onCreate() {
        super.onCreate();
        BFTApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return BFTApplication.context;
    }
}
