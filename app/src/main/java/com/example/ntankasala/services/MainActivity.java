package com.example.ntankasala.services;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ntankasala.services.BoundService.LocalBinder;
import com.example.ntankasala.services.BoundService;

public class MainActivity extends AppCompatActivity {

    Button mbroadcastIntent, mnotifyResult, mcancelService, mboundService, mipc_bt, mpendingIntent_bt;
    TextView mresult_tv;
    EditText mraw_et;
    DownloadReceiver mdownloadReceiver;
    SystemBroadcastReceiver msystemBroadcastReceiver;
    Intent service_intent, mpendingIntent, service_intent1, boundIntent, ipc_intent;

    SharedPreferences msharedpreferences;
    SharedPreferences.Editor mpreferenceEditor;

    int notification_id = 2;

    //for sending tasks to service
    Messenger serviceMessenger = null;
    boolean isIpcBound = false;

    //for getting response from service
    Messenger clientMessenger = null;

    HandlerThread clientThread= new HandlerThread("clientThread");


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

    private ServiceConnection ipcServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            clientThread.start();
            serviceMessenger = new Messenger(service);
            clientMessenger = new Messenger(new ClientHandler(clientThread.getLooper()));
            isIpcBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceMessenger = null;
            isIpcBound = false;
        }
    };

    public class ClientHandler extends Handler{

        public ClientHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MessengerIpc.SEND_RESPONSE:
                    final String ipc_response = msg.getData().getString("responseipc","No Response");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mresult_tv.setText(ipc_response);
                        }
                    });
                    break;
                default:
                    super.handleMessage(msg);
            }

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        msharedpreferences = getPreferences(Context.MODE_PRIVATE);
        mpreferenceEditor = msharedpreferences.edit();
        boundIntent = new Intent(this, BoundService.class);
        service_intent = new Intent(this, DownloadService.class);
        service_intent1 = new Intent(this, AsynchronousService.class);
        mpendingIntent = new Intent(this, DownloadService.class);
        ipc_intent = new Intent(this, MessengerIpc.class);
        mnotifyResult = (Button) findViewById(R.id.notify_bt);
        mresult_tv = (TextView) findViewById(R.id.result_tv);
        mipc_bt = (Button)findViewById(R.id.ipc_bt);
        mpendingIntent_bt = (Button)findViewById(R.id.pndngIntent_bt);
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

        //bind IPC service
        bindService(ipc_intent, ipcServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mdownloadReceiver);
        mdownloadReceiver.onDettach();
        unregisterReceiver(msystemBroadcastReceiver);

        //unbind boundservice
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }

        if(isIpcBound){
            unbindService(ipcServiceConnection);
            isIpcBound =false;
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
            case R.id.pndngIntent_bt:
                mpendingIntent.putExtra("operationid", 3);
                startPendingIntent(raw_text);break;
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

                //able to use service methods through iBinder object returned in serviceConnection Interfaces onconnected method
                if (isBound) {
                    mresult_tv.setText(boundService.convertUpper(raw_text));
                }

               /* //start bound service using startservice(intent) to get events to onStartCommand
                startService(boundIntent);*/

                break;
            case R.id.ipc_bt:

                if(isIpcBound){
                    Message msg = Message.obtain(null, MessengerIpc.CONVERT_TO_UPPERCASE, 0, 0);
                    msg.replyTo = clientMessenger;
                    Bundle b =new Bundle();
                    b.putString("raw", raw_text);
                    msg.setData(b);
                    try {
                        serviceMessenger.send(msg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 3 && resultCode == 2){
            mresult_tv.setText(data.getStringExtra("response"));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startPendingIntent(String raw_text){
        PendingIntent pendingIntent = createPendingResult(3, new Intent(),0);
        mpendingIntent.putExtra("raw",raw_text);
        mpendingIntent.putExtra("pendingintent",pendingIntent);
        startService(mpendingIntent);

    }
    private void startserviceTask(String raw_text) {
        service_intent.putExtra("raw", raw_text);
        startService(service_intent);
    }

    private void startserviceTask1(String raw_text) {

        notification_id = msharedpreferences.getInt("notificationid", 2);
        service_intent1.putExtra("raw", raw_text);
        service_intent1.putExtra("notificationid", notification_id);
        mpreferenceEditor.putInt("notificationid", notification_id + 1);
        mpreferenceEditor.commit();
        startService(service_intent1);

    }


}
