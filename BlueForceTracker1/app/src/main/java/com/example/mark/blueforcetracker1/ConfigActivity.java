package com.example.mark.blueforcetracker1;

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
        EditText serverText = (EditText) findViewById(R.id.editText);
        SharedPreferences pref = context.getSharedPreferences(BFTApplication.prefName, MODE_PRIVATE);
        serverText.setText(pref.getString(BFTApplication.serverKey, ""), TextView.BufferType.NORMAL);
    }

    public void handleRestart(View view) {
        Context context = BFTApplication.getAppContext();
        SharedPreferences pref = context.getSharedPreferences(BFTApplication.prefName, MODE_PRIVATE);
        EditText serverText = (EditText) findViewById(R.id.editText);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(BFTApplication.serverKey, serverText.getText().toString());
        editor.commit();
        Intent intent = new Intent(this, MqttPublishService.class);
        intent.setAction(MqttPublishService.ACTION_RESTART);
        startService(intent);
    }
}
