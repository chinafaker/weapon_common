package com.hhb.aidlservertest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.hhb.aidl.IAECUCallback;
import com.hhb.aidl.IOtaRemoteService;

public class OtaService extends Service {
    private static final String TAG = "OtaService";

    public OtaService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() ");
        Toast.makeText(this, "OTA远程服务启动", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() ");
        Toast.makeText(this, "OTA远程服务关闭", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() ");
        return new IOtaRemoteService.Stub() {
            @Override
            public void getEcuInfo(String ecuDID, String ecuSwID, IAECUCallback callback) throws RemoteException {
                Log.d(TAG, "getEcuInfo() ecuDID=" + ecuDID + " ecuSwID" + ecuSwID);
                callback.onFailed(1000);
            }
        };
    }
}