package com.hhb.ep21client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.abupdate.ota.IAbupOtaDataChangeCallback;
import com.abupdate.ota.IAbupOtaService;

public class MainActivity extends AppCompatActivity {
    private Intent aidlIntent;
    IAbupOtaService iOtaInter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void bindService(View view) {
        bindService();
    }

    public void getAvnVersion(View view) throws RemoteException {
        String avnMpuVersion = iOtaInter.getAvnMpuVersion();
        Log.d("TAG", "getAvnMpuVersion: " + avnMpuVersion);
    }

    public void getTboxVersion(View view) throws RemoteException {
        String tboxVersion = iOtaInter.getTboxVersion();
        Log.d("TAG", "getTboxVersion: " + tboxVersion);
    }

    public void existNewVersion(View view) throws RemoteException {
        //是否存在新版本
        boolean exist = iOtaInter.existNewVersion();
        Log.d("TAG", "existNewVersion: " + exist);
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Log.d("TAG", "bindService() ");
        aidlIntent = new Intent();
        //绑定服务端的service
        aidlIntent.setAction("com.abupdate.ota.server.OtaInterService");
        //新版本（5.0后）必须显式intent启动 绑定服务
        aidlIntent.setComponent(new ComponentName("com.abupdate.ota", "com.abupdate.ota.server.OtaInterService"));
        //绑定的时候服务端自动创建
        boolean result = bindService(aidlIntent, conn, Context.BIND_AUTO_CREATE);
    }

    private boolean onServiceConnected = false;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TAG", "onServiceConnected: ");
            onServiceConnected = true;
            iOtaInter = IAbupOtaService.Stub.asInterface(service);
            try {
                iOtaInter.registerAbupOtaDataChangeCallback(new IAbupOtaDataChangeCallback.Stub() {
                    @Override
                    public void onNewVersionChange(boolean exist) throws RemoteException {
                        Log.i("TAG", "onNewVersionChange() exist=" + exist);
                    }

                    @Override
                    public void onAvnMpuVersionChange(String version) throws RemoteException {
                        Log.i("TAG", "onAvnMpuVersionChange() version=" + version);
                    }

                    @Override
                    public void onTboxVersionChange(String version) throws RemoteException {
                        Log.i("TAG", "onTboxVersionChange() version=" + version);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            try {
                //是否存在新版本
                boolean exist = iOtaInter.existNewVersion();
                Log.d("TAG", "existNewVersion: " + exist);

                String avnMpuVersion = iOtaInter.getAvnMpuVersion();
                Log.d("TAG", "getAvnMpuVersion: " + avnMpuVersion);

                String tboxMpuVersion = iOtaInter.getTboxMpuVersion();
                Log.d("TAG", "getTboxMpuVersion: " + tboxMpuVersion);

                String tboxMcuVersion = iOtaInter.getTboxMcuVersion();
                Log.d("TAG", "getTboxMcuVersion: " + tboxMcuVersion);

                String tboxVersion = iOtaInter.getTboxVersion();
                Log.d("TAG", "getTboxVersion: " + tboxVersion);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("TAG", "onServiceDisconnected: ");
            onServiceConnected = false;
            iOtaInter = null;
//            while (!onServiceConnected) {
//                try {
//                    Thread.sleep(200);
//                    bindService();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    };

}