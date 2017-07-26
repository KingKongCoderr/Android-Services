package com.example.ntankasala.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Created by ntankasala on 7/24/17.
 */

public class MessengerIpc extends Service {

    static final int CONVERT_TO_UPPERCASE = 1;
    static final int SEND_RESPONSE = 2;

    HandlerThread serviceThread ;

    Messenger serviceMessenger ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        serviceThread =  new HandlerThread("serviceThread");
        serviceThread.start();
        serviceMessenger = new Messenger(new ServiceHandler(serviceThread.getLooper()));
        return serviceMessenger.getBinder();
    }

    public class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONVERT_TO_UPPERCASE:
                    Messenger clientMessenger = msg.replyTo;
                    Message clientMsg = Message.obtain(null,SEND_RESPONSE,0,0);
                    Bundle client_bundle= new Bundle();
                    client_bundle.putString("responseipc",convertUpper(msg.getData().getString("raw", "sample")));
                    clientMsg.setData(client_bundle);
                    try {
                        clientMessenger.send(clientMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public String convertUpper(String msg) {
        String upper_string = msg.toUpperCase();
        return upper_string;
    }

}
