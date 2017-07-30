package com.example.ntankasala.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.textformatter.Formatter;

/**
 * Created by ntankasala on 7/21/17.
 */

public class BoundService extends Service {
    private final IBinder mBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder{
         BoundService getService(){
            return BoundService.this;
        }
    }

    // TODO: 7/24/17 do this in a seperate thread using executor framework
    public String convertUpper(String msg){
        Formatter upperCaseFormatter = new Formatter(msg);
        return upperCaseFormatter.getFormated();
    }



}
