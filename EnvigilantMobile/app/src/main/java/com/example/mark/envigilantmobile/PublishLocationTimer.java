package com.example.mark.envigilantmobile;

import com.example.mark.envigilantmobile.PublishLocation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PublishLocationTimer extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        PublishLocation pub = new PublishLocation(context);
        pub.run();
    }
}
