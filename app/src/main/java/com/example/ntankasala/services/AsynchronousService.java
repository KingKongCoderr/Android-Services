package com.example.ntankasala.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by ntankasala on 7/19/17.
 */

public class AsynchronousService extends Service {


    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //for every request we are creating new thread for simultaneous execution of various service tasks
        HandlerThread mhandlerThread = new HandlerThread("extendingService", Process.THREAD_PRIORITY_DEFAULT);
        mhandlerThread.start();
        mServiceLooper = mhandlerThread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        String raw = intent.getStringExtra("raw");
        int notificaiton_id = intent.getIntExtra("notificationid", 2);
        mServiceHandler.post(new ServiceRunnable(raw,notificaiton_id));
        return START_STICKY;
      /*START_NOT_STICKY
        If the system kills the service after onStartCommand() returns, do not recreate the service unless there are pending intents to deliver. This is the safest option to avoid running your service when not necessary and when your application can simply restart any unfinished jobs.
        START_STICKY
        If the system kills the service after onStartCommand() returns, recreate the service and call onStartCommand(), but do not redeliver the last intent. Instead, the system calls onStartCommand() with a null intent unless there are pending intents to start the service. In that case, those intents are delivered. This is suitable for media players (or similar services) that are not executing commands but are running indefinitely and waiting for a job.
        START_REDELIVER_INTENT
        If the system kills the service after onStartCommand() returns, recreate the service and call onStartCommand() with the last intent that was delivered to the service. Any pending intents are delivered in turn. This is suitable for services that are actively performing a job that should be immediately resumed, such as downloading a file.
        For more details about these return values, see the linked reference documentation for each constant.*/




    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public class ServiceRunnable implements Runnable{
        private NotificationCompat.Builder notification_builder;
        private NotificationManager notificationManager;
        String result;
        int id;
        public ServiceRunnable(String msg, int notification_id) {
            this.result = msg + " is being serviced";
            this.id = notification_id;
        }

        @Override
        public void run() {
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notification_builder = new NotificationCompat.Builder(getApplicationContext());
            notification_builder.setContentTitle("Service 1 is running")
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
                    Thread.sleep(1 * 400);
                } catch (InterruptedException e) {
                    Log.d("Intentservice", "sleep failure");
                }
            }
            // When the loop is finished, updates the notification
            notification_builder.setContentText("Notifying complete")
                    // Removes the progress bar
                    .setProgress(0,0,false);
            notificationManager.notify(id,notification_builder.build());
            // broadcastResult(result);


        }
    }
}
