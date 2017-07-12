package com.example.mark.envigilantmobile;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Context context = EnvigilantMobileApplication.getAppContext();
        EditText eText = (EditText) findViewById(R.id.serverAddrText);
        SharedPreferences pref = EnvigilantMobileApplication.getPreferences();
        eText.setText(pref.getString(EnvigilantMobileApplication.serverKey, ""), TextView.BufferType.NORMAL);
        eText = (EditText) findViewById(R.id.portText);
        eText.setText(pref.getString(EnvigilantMobileApplication.portNumber, "1883"), TextView.BufferType.NORMAL);
        eText = (EditText) findViewById(R.id.regText);
        eText.setText(pref.getString(EnvigilantMobileApplication.regRefreshFreq, "60"), TextView.BufferType.NORMAL);
        eText = (EditText) findViewById(R.id.accelText);
        eText.setText(pref.getString(EnvigilantMobileApplication.accRefreshFreq, "15"), TextView.BufferType.NORMAL);
        eText = (EditText) findViewById(R.id.sensorPathText);
        eText.setText(pref.getString(EnvigilantMobileApplication.sensorPath, ""), TextView.BufferType.NORMAL);
    }

    public void handleRestart(View view) {
        Context context = EnvigilantMobileApplication.getAppContext();
        SharedPreferences pref = EnvigilantMobileApplication.getPreferences();
        EditText serverText = (EditText) findViewById(R.id.serverAddrText);
        EditText portText = (EditText) findViewById(R.id.portText);
        EditText regRefreshText = (EditText) findViewById(R.id.regText);
        EditText accRefreshText = (EditText) findViewById(R.id.accelText);
        EditText sensorText = (EditText) findViewById(R.id.sensorPathText);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(EnvigilantMobileApplication.serverKey, serverText.getText().toString());
        editor.putString(EnvigilantMobileApplication.portNumber, portText.getText().toString());
        editor.putString(EnvigilantMobileApplication.regRefreshFreq, regRefreshText.getText().toString());
        editor.putString(EnvigilantMobileApplication.accRefreshFreq, accRefreshText.getText().toString());
        editor.putString(EnvigilantMobileApplication.sensorPath, sensorText.getText().toString());
        editor.commit();
        int refresh = Integer.parseInt(regRefreshText.getText().toString());
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PublishLocationTimer.class);
        PendingIntent cpi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(cpi);
        PublishLocation pub = new PublishLocation(context);
        pub.run();
        editor = pref.edit();
        editor.putBoolean(EnvigilantMobileApplication.currentState, true);
        editor.commit();
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), refresh*1000, pi);
        System.out.println("Setting alarm for every " + refresh*1000 + " milliseconds");
    }

    public void handleCancel(View view) {
        Context context = EnvigilantMobileApplication.getAppContext();
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PublishLocationTimer.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarm.cancel(pi);
        SharedPreferences pref = EnvigilantMobileApplication.getPreferences();
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(EnvigilantMobileApplication.currentState, false);
        editor.commit();
        System.out.println("Canceled alarm");
    }
}
