package com.example.mark.blueforcetracker1;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    private static final Integer threadPool = 2;
    private static ScheduledThreadPoolExecutor sched = null;
    private static boolean isRunning = false;

    public MqttPublishService() {
        super("MqttPublishService");
        sched = new ScheduledThreadPoolExecutor(threadPool);
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
        final String action = intent.getAction();
        if (ACTION_RESTART.equals(action)) {
            handleActionRestart(/* "tcp://" + server + ":" + port, topic */);
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRestart(/* String server, String topic */) {
        Context context = BFTApplication.getAppContext();
        SharedPreferences pref = context.getSharedPreferences(BFTApplication.prefName, android.content.Context.MODE_PRIVATE);
        Integer refresh = Integer.parseInt(pref.getString(BFTApplication.refreshFreq, "60"));
        PublishLocation r = new PublishLocation(this);
        // TODO: schedule the Runnable
        if (isRunning) {
            sched.shutdown();
            sched = new ScheduledThreadPoolExecutor(threadPool);
        }
        sched.scheduleAtFixedRate(r, refresh, refresh, TimeUnit.SECONDS);
        isRunning = true;
    }

}
