package com.example.mark.envigilantmobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by mark on 6/18/17.
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

    PublishLocation(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        try {
            if (!mLocConnected || mGoogleApiClient == null)
                connectToLocationService();

            if (publisher == null)
                connectToQueue();
        } catch (Exception e) {
            System.out.println("Exception caught: " + e.getMessage());
            e.printStackTrace();
        }
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
                JSONObject latitude = new JSONObject()
                        .put("field", "LATITUDE")
                        .put("amount", lat)
                        .put("attributes", new JSONObject());
                JSONObject longitude = new JSONObject()
                        .put("field", "LONGITUDE")
                        .put("amount", lng)
                        .put("attributes", new JSONObject());
                JSONObject accuracy = new JSONObject()
                        .put("field", "accuracy")
                        .put("amount", acc)
                        .put("attributes", new JSONObject());
                JSONArray jargs = new JSONArray()
                        .put(latitude)
                        .put(longitude)
                        .put(accuracy);
                publisher.publishValues(jargs);
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
            Context context = EnvigilantMobileApplication.getAppContext();
            SharedPreferences pref = context.getSharedPreferences(EnvigilantMobileApplication.prefName, android.content.Context.MODE_PRIVATE);
            server = pref.getString(EnvigilantMobileApplication.serverKey, "");
            port = pref.getString(EnvigilantMobileApplication.portNumber, "1883");
            topic = pref.getString(EnvigilantMobileApplication.queueName, "");
            sensor = pref.getString(EnvigilantMobileApplication.sensorPath, "");

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
        try {
            if (mGoogleApiClient == null)
                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            mGoogleApiClient.connect();
        } catch (Exception e) {
            System.out.println("Error caugh in connectToLocationService: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void publishStatus(String status, String descr) {
        if (publisher == null)
            connectToQueue();

        if (publisher != null)
            publisher.publishStatus(status, descr);
    }

}
