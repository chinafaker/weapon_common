package com.hhb.ep21client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.abupdate.common.Trace;
import com.abupdate.common_download.DownConfig;
import com.abupdate.common_download.DownEntity;
import com.abupdate.common_download.DownManager;
import com.abupdate.common_download.DownTask;
import com.abupdate.common_download.listener.SimpleDownListener;
import com.abupdate.common_download.verifymode.Impl.VerifySha256Impl;
import com.abupdate.ota.IAbupOtaDataChangeCallback;
import com.abupdate.ota.IAbupOtaService;


import java.io.File;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Intent aidlIntent;
    IAbupOtaService iOtaInter;
    private ScrollView scrollView;
    private TextView tv_logcat;
    private StringBuilder builder = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollView = findViewById(R.id.scrollView);
        tv_logcat = findViewById(R.id.tv_logcat);
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

    public void startDownLoad(View view) {
        Trace.i(TAG, "startDownLoad()");
        String url = "https://fota-down-eu.soimt.com/%2Fdownload/strategy/test/language/181d4d0122204652a6287f5d98cf1ca5.xml?AWSAccessKeyId=AKIAZEN2DFU6Q54HXFYT&Expires=1608619240&Signature=ertE2oI0TAlX1owm2HhC8jd0MKE%3D";
        String path = getExternalCacheDir().getAbsolutePath() + "/package";
        Trace.i(TAG, "download() url is " + url);
        Trace.i(TAG, "download() path is " + path);
        print("startDownLoad()");
        print("download() url is " + url);
        print("download() path is " + path);
        File file = new File(path);
        if (file.isFile() || file.isDirectory()) {
            file.delete();
        }

        DownloadUtil.get().download(url, path, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Trace.i(TAG, "onDownloadSuccess()");
                print("onDownloadSuccess()");
            }

            @Override
            public void onDownloading(int progress) {
                Trace.i(TAG, "onDownloading() progress is " + progress);
                print("onDownloading() progress is" + progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                Trace.i(TAG, "onDownloadFailed() e =" + e.getMessage());
                print("onDownloadFailed() Exception e is" + e.getMessage());
            }
        });
    }


    public void startDownLoad2(View view) {
        print("startDownLoad2");
        new Thread(() -> execute()).start();

    }

    public void clearLog(View view) {
        builder.setLength(0);
        tv_logcat.setText(builder);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void execute() {
        Trace.d(TAG, "MultiLanguageTask execute() start");
        String languageFileUrl = "https://fota-down-eu.soimt.com/%2Fdownload/strategy/test/language/181d4d0122204652a6287f5d98cf1ca5.xml?AWSAccessKeyId=AKIAZEN2DFU6Q54HXFYT&Expires=1608619240&Signature=ertE2oI0TAlX1owm2HhC8jd0MKE%3D";
        String languageFileHash = "3b64b634960af2815c52c61022e90106a17b16428b10fe8d7ef9bd2a5326d5c2";
        String filePath = getExternalCacheDir().getAbsolutePath() + "/package2/multiLanguage.xml";

        print("execute url is " + languageFileUrl);
        print("execute hash is " + languageFileHash);
        print("execute filePath is " + filePath);
        File file = new File(filePath);
        if (file.isFile() || file.isDirectory()) {
            file.delete();
        }

        boolean downloadFile = downloadFile(languageFileUrl, filePath, languageFileHash);
        if (downloadFile) {
            Trace.e(TAG, "execute() download multi language file success");
            print("execute() download multi language file success");
        } else {
            Trace.e(TAG, "execute() download multi language file fail");
            print("execute() download multi language file fail");
        }
    }


    public boolean downloadFile(String xmlUrl, String filePath, String languageFileHash) {
        Trace.d(TAG, "downloadFile() start，url:%s, filePath:%s", xmlUrl, filePath);
        DownConfig.setVerifyMode(VerifySha256Impl.INSTANCE);
        DownEntity downEntity = new DownEntity()
                .setFilePath(filePath)
                .setVerifyCode(languageFileHash)
                .setUrl(xmlUrl);
        DownTask downTask = new DownTask();
        downTask.add(downEntity);
        final boolean[] hasGot = {false};
        final boolean[] success = {false};
        downTask.setListener(new SimpleDownListener() {
            @Override
            public void on_finished(List<DownEntity> successDownEntities, List<DownEntity> failedDownEntities) {
                super.on_finished(successDownEntities, failedDownEntities);
                if (successDownEntities.size() > 0) {
                    Trace.d(TAG, "download success");
                    print("download success");
                    success[0] = true;
                } else {
                    Trace.d(TAG, "download fail");
                    print("download fail");
                }
                hasGot[0] = true;
            }
        });
        DownManager.execAsync(downTask);
        while (!hasGot[0]) {
            SystemClock.sleep(10);
        }
        return success[0];
    }

    private void print(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.append(message)
                        .append("\r\n")
                        .append("\r\n");
                tv_logcat.setText(builder.toString());
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

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


    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TAG", "onServiceConnected: ");
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
            iOtaInter = null;
        }
    };

}