package com.example.ntankasala.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.Toast;

/**
 * Created by ntankasala on 7/18/17.
 */

public class SystemBroadcastReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        int state = intent.getIntExtra("state",-1);
        if(state == 1) {
            Toast.makeText(context, "Headset pluged in", Toast.LENGTH_SHORT).show();
            Intent service_intent = new Intent(context, DownloadService.class);
            service_intent.putExtra("raw", "HEADSET");
            service_intent.putExtra("operationid", 2);
            context.startService(service_intent);
        }
    }

}
