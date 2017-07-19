package com.example.ntankasala.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ntankasala on 7/18/17.
 */

public class SystemBroadcastReceiver extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service_intent = new Intent(context, DownloadService.class);
        service_intent.putExtra("raw","HEADSET");
        service_intent.putExtra("operationid",2);
        context.startService(service_intent);
    }
}
