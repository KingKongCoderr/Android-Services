package com.example.ntankasala.services;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity  {

    Button mbroadcastIntent, mnotifyResult;
    TextView mresult_tv;
    EditText mraw_et;
    DownloadReceiver mdownloadReceiver;
    SystemBroadcastReceiver msystemBroadcastReceiver;
    Intent service_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        service_intent = new Intent(this, DownloadService.class);
        mnotifyResult = (Button) findViewById(R.id.notify_bt);
        mresult_tv = (TextView) findViewById(R.id.result_tv);
        mbroadcastIntent = (Button) findViewById(R.id.download_bt);
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
        headset.addAction(BatteryManager.ACTION_CHARGING);
        registerReceiver(msystemBroadcastReceiver, headset);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mdownloadReceiver);
        mdownloadReceiver.onDettach();
        unregisterReceiver(msystemBroadcastReceiver);

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

        }
    }

    private void startserviceTask(String raw_text) {
        service_intent.putExtra("raw", raw_text);
        startService(service_intent);
    }
}
