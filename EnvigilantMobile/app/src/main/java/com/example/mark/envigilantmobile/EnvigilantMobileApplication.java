package com.example.mark.envigilantmobile;

import android.app.Application;
import android.content.Context;

/**
 * Created by mark on 6/18/17.
 */

public class EnvigilantMobileApplication extends Application {
    private static Context context;
    public static final String prefName = "EnvigilantMobilePreferences";
    public static final String serverKey = "EnvigilantMobile.server";
    public static final String portNumber = "EnvigilantMobile.port";
    public static final String queueName = "EnvigilantMobile.queue";
    public static final String refreshFreq = "EnvigilantMobile.refresh";
    public static final String sensorPath = "EnvigilantMobile.sensor";

    @Override
    public void onCreate() {
        super.onCreate();
        EnvigilantMobileApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return EnvigilantMobileApplication.context;
    }
}
