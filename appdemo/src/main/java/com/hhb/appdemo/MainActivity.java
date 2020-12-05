package com.hhb.appdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hhb.aidl.AECU;
import com.hhb.aidl.IAECUCallback;
import com.hhb.aidl.IOtaRemoteService;
import com.hhb.common.Trace;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private IOtaRemoteService iOtaRemoteService = null;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Trace.i(TAG, "onServiceConnected()");
            iOtaRemoteService = IOtaRemoteService.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Trace.i(TAG, "onServiceDisconnected()");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Trace.i(TAG, "onCreate()");
    }

    public void bindService(View view) {
        Trace.i(TAG, "bindService()");
        Intent intent = new Intent("com.hhb.aidl.IOtaRemoteService");
        intent.setPackage("com.hhb.aidlservertest");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    public void getEcu(View view) throws RemoteException {
        Trace.i(TAG, "getEcu()");
        if (null != iOtaRemoteService) {
            iOtaRemoteService.getEcuInfo("", "", new IAECUCallback.Stub() {
                @Override
                public void onSuccess(AECU ecu) throws RemoteException {
                    Trace.i(TAG, "onSuccess()");
                }

                @Override
                public void onFailed(int error) throws RemoteException {
                    Trace.i(TAG, "onFailed() error is " + error);
                }
            });
        }
    }
}