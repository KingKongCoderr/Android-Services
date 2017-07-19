package com.example.ntankasala.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by ntankasala on 7/18/17.
 */

public class DownloadService extends IntentService {

    public static final String DOWNLOADBROADCAST = "com.example.ntankasala.services.DOWNLOADBROADCAST" ;
    public static final String RESULT = "serviced";

    private NotificationCompat.Builder notification_builder;
    private NotificationManager notificationManager;

    private int id = 1;

    public DownloadService() {
        super("Download Thread");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        String raw = intent.getStringExtra("raw") + " is serviced";
        int operation_id = intent.getIntExtra("operationid", 1);
        switch (operation_id){
            case 1:broadcastResult(raw);break;
            case 2:notifyResult(raw);break;
        }

    }

    public void broadcastResult(String result){
        Intent broadcast_intent = new Intent(DOWNLOADBROADCAST );
        broadcast_intent.putExtra(RESULT, result);
        sendBroadcast(broadcast_intent);
    }

    public void notifyResult(String result){
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notification_builder = new NotificationCompat.Builder(this);
        notification_builder.setContentTitle("Notifying the result")
                            .setContentText(result).setSmallIcon(R.drawable.notification);

        int incr;
        // Do the "lengthy" operation 20 times
        for (incr = 0; incr <= 100; incr+=5) {
            // Sets the progress indicator to a max value, the
            // current completion percentage, and "determinate"
            // state
            notification_builder.setProgress(100, incr, false);
            // Displays the progress bar for the first time.
            notificationManager.notify(id, notification_builder.build());
            // Sleeps the thread, simulating an operation
            // that takes time
            try {
                // Sleep for 5 seconds
                Thread.sleep(1 * 500);
            } catch (InterruptedException e) {
                Log.d("Intentservice", "sleep failure");
            }
        }
        // When the loop is finished, updates the notification
        notification_builder.setContentText("Notifying complete")
                // Removes the progress bar
                .setProgress(0,0,false);
        notificationManager.notify(id,notification_builder.build());
        broadcastResult(result);

    }
}
