package com.example.mark.blueforcetracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class PublishService extends Service {
    public PublishService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = BFTApplication.getAppContext();
        Toast.makeText(context, "Starting the Service", Toast.LENGTH_LONG).show();
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
