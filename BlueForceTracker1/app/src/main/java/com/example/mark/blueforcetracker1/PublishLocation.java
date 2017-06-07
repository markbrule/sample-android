package com.example.mark.blueforcetracker1;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by mark on 5/30/17.
 */

public class PublishLocation implements Runnable, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static SdwMqttClient publisher = null;
    public static GoogleApiClient mGoogleApiClient = null;
    private static boolean mLocConnected = false;
    private static String server;
    private static String port;
    private static String topic;
    private static String sensor;
    private Context context;

    PublishLocation(Context ctxt) {
        context = ctxt;
    }

    // Runnable interface
    @Override
    public void run() {
        if (! mLocConnected || mGoogleApiClient == null)
            connectToLocationService();

        if (publisher == null)
            connectToQueue();

        if (publisher == null) {
            // TODO: publisher failed
        } else {
            try {
                Double lat = 0.0, lng = 0.0, acc=0.0;
                if (mLocConnected) {
                    try {
                        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        if (lastLocation != null) {
                            lat = lastLocation.getLatitude();
                            lng = lastLocation.getLongitude();
                            acc = (double) lastLocation.getAccuracy();
                        }
                    } catch (SecurityException e) {
                        // TODO: alert user of security exception
                        mLocConnected = false;
                    }
                }
                List<Map<String,Object>> args = new ArrayList<Map<String,Object>>(2);
                Map<String,Object> latMap = new HashMap<String,Object>();
                latMap.put("field", "LATITUDE");
                latMap.put("amount", lat);
                latMap.put("attributes", null);
                Map<String,Object> longMap = new HashMap<String,Object>();
                longMap.put("field", "LONGITUDE");
                longMap.put("amount", lng);
                longMap.put("attributes", null);
                Map<String,Object> accMap = new HashMap<String,Object>();
                accMap.put("field", "accuracy");
                accMap.put("amount", acc);
                accMap.put("attributes", null);
                args.add(latMap);
                args.add(longMap);
                args.add(accMap);
                publisher.publishValues(args);
            } catch (Exception e) {
                // TODO: handle the exception
                System.out.println("Error publishing the location: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // GoogleApiClient interfaces
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mLocConnected = false;
        // TODO: if suspended, what happens when resumed? Does onConnected get called?
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mLocConnected = false;
        // TODO: alert user to the failure
    }

    private void connectToQueue() {
        try {
            Context context = BFTApplication.getAppContext();
            SharedPreferences pref = context.getSharedPreferences(BFTApplication.prefName, android.content.Context.MODE_PRIVATE);
            server = pref.getString(BFTApplication.serverKey, "");
            port = pref.getString(BFTApplication.portNumber, "1883");
            topic = pref.getString(BFTApplication.queueName, "");
            sensor = pref.getString(BFTApplication.sensorPath, "");

            // TODO: Added here to replace paho client
            publisher = new SdwMqttClient(server, port, sensor);
            publisher.publishStatus("RUNNING", "");
            System.out.println("Created the publisher");
        } catch (Exception e) {
            System.out.println("Error caught creating the publisher");
            e.printStackTrace();
        }
    }

    private void connectToLocationService() {
        if (mGoogleApiClient == null)
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    //.onConnectionFailedListener(this) TODO: what about failed listeners?
                    .addApi(LocationServices.API)
                    .build();
        mGoogleApiClient.connect();
    }

    public void stopPublishing() {
        if (publisher != null)          // no need to stop if the publisher isn't made yet
            publisher.publishStatus("NOT_RUNNING", "");
    }
}
