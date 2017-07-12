package com.example.mark.envigilantmobile;

import com.example.mark.envigilantmobile.PublishLocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PublishLocationTimer extends BroadcastReceiver {

    private static long last_time = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        long now = System.currentTimeMillis();
        System.out.println("Woken up after " + (now-last_time) + " milliseconds");
        last_time = now;
        // an Intent broadcast.
        PublishLocation pub = new PublishLocation(context);
        pub.run();
    }
}
