package com.example.ntankasala.services;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ntankasala.services.BoundService.LocalBinder;
import com.example.ntankasala.services.BoundService;

public class MainActivity extends AppCompatActivity  {

    Button mbroadcastIntent, mnotifyResult, mcancelService, mboundService;
    TextView mresult_tv;
    EditText mraw_et;
    DownloadReceiver mdownloadReceiver;
    SystemBroadcastReceiver msystemBroadcastReceiver;
    Intent service_intent, service_intent1, boundIntent;

    SharedPreferences msharedpreferences;
    SharedPreferences.Editor mpreferenceEditor;

    int notification_id = 2;

    BoundService boundService;
    boolean isBound = false;


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LocalBinder mBinder = (LocalBinder) iBinder;
            boundService = mBinder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msharedpreferences = getPreferences(Context.MODE_PRIVATE);
        mpreferenceEditor = msharedpreferences.edit();
        boundIntent = new Intent(this, BoundService.class);
        service_intent = new Intent(this, DownloadService.class);
        service_intent1 = new Intent(this, AsynchronousService.class);
        mnotifyResult = (Button) findViewById(R.id.notify_bt);
        mresult_tv = (TextView) findViewById(R.id.result_tv);
        mbroadcastIntent = (Button) findViewById(R.id.download_bt);
        mcancelService = (Button) findViewById(R.id.cancelservice_bt);
        mboundService = (Button) findViewById(R.id.boundservice_bt);
        mraw_et = (EditText) findViewById(R.id.raw_et);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mdownloadReceiver = new DownloadReceiver();
        msystemBroadcastReceiver = new SystemBroadcastReceiver();
        IntentFilter intent_filter = new IntentFilter(DownloadService.DOWNLOADBROADCAST);
        registerReceiver(mdownloadReceiver, intent_filter);
        mdownloadReceiver.onAttach((TextView) findViewById(R.id.result_tv));
        IntentFilter headset = new IntentFilter("android.intent.action.HEADSET_PLUG");
        registerReceiver(msystemBroadcastReceiver, headset);

        //bind boundservice
        bindService(boundIntent, serviceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mdownloadReceiver);
        mdownloadReceiver.onDettach();
        unregisterReceiver(msystemBroadcastReceiver);

        //unbind boundservice
        if(isBound){
            unbindService(serviceConnection);
            isBound = false;
        }
    }


    public void buttonClick(View view) {
        int id = view.getId();
        String raw_text = mraw_et.getText().toString();
        switch (id) {
            case R.id.download_bt:
                service_intent.putExtra("operationid", 1);
                startserviceTask(raw_text);
                break;
            case R.id.notify_bt:
                service_intent.putExtra("operationid", 2);
                startserviceTask(raw_text);
                break;
            case R.id.notify2_bt:
                service_intent1.putExtra("operationid", 3);
                startserviceTask1(raw_text);
                break;
            case R.id.cancelservice_bt:
                stopService(service_intent);
                break;
            case R.id.boundservice_bt:
                //start bound service using startservice(intent) to get events to onStartCommand
                startService(boundIntent);

                break;

        }
    }

    private void startserviceTask(String raw_text) {
        service_intent.putExtra("raw", raw_text);
        startService(service_intent);
    }

    private void startserviceTask1(String raw_text){

        notification_id = msharedpreferences.getInt("notificationid", 2);
        service_intent1.putExtra("raw", raw_text);
        service_intent1.putExtra("notificationid", notification_id);
        mpreferenceEditor.putInt("notificationid", notification_id+1);
        mpreferenceEditor.commit();
        startService(service_intent1);

    }


}
