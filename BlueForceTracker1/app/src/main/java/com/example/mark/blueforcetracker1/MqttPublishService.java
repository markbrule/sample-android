package com.example.mark.blueforcetracker1;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service ogit hn a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MqttPublishService extends IntentService {
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_RESTART = "com.example.mark.blueforcetracker1.action.RESTART";

    public MqttPublishService() {
        super("MqttPublishService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRestart(Context context) {
        Intent intent = new Intent(context, MqttPublishService.class);
        intent.setAction(ACTION_RESTART);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Context context = BFTApplication.getAppContext();
            SharedPreferences pref = context.getSharedPreferences(BFTApplication.prefName, MODE_PRIVATE);
            String server = "From MqttPublishService: " + pref.getString(BFTApplication.serverKey, "");

            final String action = intent.getAction();
            if (ACTION_RESTART.equals(action)) {
                handleActionRestart(server);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRestart(String server) {
        // TODO: restart the scheduler
    }

}
