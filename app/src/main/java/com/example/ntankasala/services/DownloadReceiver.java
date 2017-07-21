package com.example.ntankasala.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by ntankasala on 7/18/17.
 */

public class DownloadReceiver extends BroadcastReceiver {

    TextView mresult_tv;

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "Insided download receiver", Toast.LENGTH_LONG).show();
        if(mresult_tv != null){
            String result = intent.getStringExtra(DownloadService.RESULT);
            mresult_tv.setText(result);
        }


    }

    public void onAttach(@Nullable TextView view){
        this.mresult_tv = view;
    }

    public void onDettach(){
        this.mresult_tv = null;
    }



}
