package com.example.mark.blueforcetracker1;

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
import android.widget.Toast;

public class ConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Context context = BFTApplication.getAppContext();
        EditText eText = (EditText) findViewById(R.id.editText);
        SharedPreferences pref = context.getSharedPreferences(BFTApplication.prefName, MODE_PRIVATE);
        eText.setText(pref.getString(BFTApplication.serverKey, ""), TextView.BufferType.NORMAL);
        eText = (EditText) findViewById(R.id.portText);
        eText.setText(pref.getString(BFTApplication.portNumber, "1883"), TextView.BufferType.NORMAL);
        eText = (EditText) findViewById(R.id.refreshFreqText);
        eText.setText(pref.getString(BFTApplication.refreshFreq, "60"), TextView.BufferType.NORMAL);
        eText = (EditText) findViewById(R.id.sensorPathText);
        eText.setText(pref.getString(BFTApplication.sensorPath, ""), TextView.BufferType.NORMAL);
        TextView vText = (TextView) findViewById(R.id.statusTextView);
        vText.setText("Publishing is OFF");
    }

    public void handleRestart(View view) {
        Context context = BFTApplication.getAppContext();
        SharedPreferences pref = context.getSharedPreferences(BFTApplication.prefName, MODE_PRIVATE);
        EditText serverText = (EditText) findViewById(R.id.editText);
        EditText portText = (EditText) findViewById(R.id.portText);
        EditText refreshText = (EditText) findViewById(R.id.refreshFreqText);
        EditText sensorText = (EditText) findViewById(R.id.sensorPathText);
        TextView statusText = (TextView) findViewById(R.id.statusTextView);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(BFTApplication.serverKey, serverText.getText().toString());
        editor.putString(BFTApplication.portNumber, portText.getText().toString());
        editor.putString(BFTApplication.refreshFreq, refreshText.getText().toString());
        editor.putString(BFTApplication.sensorPath, sensorText.getText().toString());
        editor.commit();
        int refresh = Integer.parseInt(refreshText.getText().toString());
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PublishDataTimer.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.cancel(pi);
        PublishLocation pub = new PublishLocation(context);
        pub.publishStatus("RUNNING", "");
        pub.run();
        statusText.setText("Publishing is OFF");
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), refresh*1000, pi);
        statusText.setText("Publishing is ON");
    }

    public void handleStop(View view) {
        Context context = BFTApplication.getAppContext();
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PublishDataTimer.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        TextView statusText = (TextView) findViewById(R.id.statusTextView);
        alarm.cancel(pi);
        PublishLocation pub = new PublishLocation(context);
        pub.publishStatus("NOT_RUNNING", "");
        statusText.setText("Publishing is OFF");
    }
}
