package com.hhb.ep21client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.abupdate.common.ContextVal;
import com.abupdate.common.FileUtil;
import com.abupdate.common.Trace;
import com.abupdate.common_download.DownConfig;
import com.abupdate.common_download.DownEntity;
import com.abupdate.common_download.DownManager;
import com.abupdate.common_download.DownTask;
import com.abupdate.common_download.listener.SimpleDownListener;
import com.abupdate.common_download.verifymode.Impl.VerifySha256Impl;
import com.abupdate.iot_download_libs.DLManager;
import com.abupdate.iot_download_libs.DownSimpleListener;
import com.abupdate.ota.IAbupOtaDataChangeCallback;
import com.abupdate.ota.IAbupOtaService;

import java.io.File;
import java.util.List;

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
        DLManager.getInstance().setContext(getApplicationContext());
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
        String url = "https://otadown-eu-pp.ai-ways.com/download/manage/sss/9e511a80cecb4871b5da4fd2c0457361.xml?X-Amz-Security-Token=IQoJb3JpZ2luX2VjED4aDGV1LWNlbnRyYWwtMSJHMEUCIQCBUhe3UmdR5O7WJ%2Bsk6C6qzHvBI22CiQ088hLgeDO0FQIgM%2Fxfc3gzc4y51X9TyIxvaYZiqDPbBLDgJOo5Eonp0QEqwwMI5%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARABGgw4MjAwMjg1NDY5MDUiDGlXY8MXasU8blO7oiqXA2xw6NJBBh5DwGGYdXoz9U9UR5rpijy0FsmB7u14xGH6tG1HpkrYhywD1krfrm5vlW%2FFHHLdgnSbLbWMpw9EmqEQBNEPI6BIxmz9a8XAKuv5qTV6FyEeLLBZiEeYCaK7Q9njBHDtXi9Q3gTmavfjoUHenc2AM5%2FbOzEGsIZk9Q5GuTnS3ehov2wP7aiUbEeAM354Efw4noLVtrzfBZE%2FU%2B6OmrrmBGpfg182dYxHAPbDOelQAQpVFH6pHJ3Hh%2Bgt%2FXUhM6IzcZxvkVGVNGoglpciGnsbWKoTA%2FdIMa97Nnb9J0Y6el5MUZq6PepEeGb73%2Bmz1hrMCdVpaYx75NE7n8W4DrDEqYZtvfUGSh4ArieNEp1qrH0KNbi60%2FizZdN10nIeZaD%2Fm3YssRF6NZUhdlKivkOvaxRIJbcE6TipE1dWiBsGVN2UxP5e7clsCZQ9Dt9Y7MPFnOjvZb1%2FpBeFi2BEvwQH%2F0Rg5et08iA%2FLcfE3p2QD0YkuMLTKnikNU%2BZaOLwW8yBPNY%2B9pV12SWnnDxDlvH41u6HMMPvlf8FOusBcNleVZRG2tg%2BWqhzK%2BZaLbtu5jRnd%2FElDiUwtXn61EYbwSYYv1g%2FSClIhiEucR90HeTb%2FEM234dCWO2VztlJZMN%2BcNl%2Bg4I9HKIhBOLYyCDtRsMAj%2FbWlvgIP9TiviXmjs0zRLG80C%2BclSe9Byrp47JCDQXKrn4w3XTmsHqptZgfBvtakKMXgi0wvFrEGwcKcldeKqNqCCGBqvYC4HYctrYZseXLrU9A8B23NfTTnASItc%2BBBQBQe4DQhH60P91N3PAAVUfJi6NddG82nox%2BvJt0FNh4O%2BKxtpefiyTT80YJUzrH4U9%2F46rMFw%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20201225T061835Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604800&X-Amz-Credential=ASIA353MCT5M5QXDG4F3%2F20201225%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=808243695d9d60a40d52040752076f4ddb1b851d277c59c276809ef8db26812d";
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

    public void startDownLoad3(View view) {
        print("startDownLoad3");
        String filePath = this.getExternalCacheDir().getAbsolutePath() + "/package3/multiLanguage.xml";
        Trace.d(TAG, "startDownLoad3() filePath is %s", filePath);
        print("startDownLoad3 filePath is " + filePath);
        File file = new File(filePath);
        if (file.exists()) {
            Trace.i(TAG, "startDownLoad3() file delete " + file.delete());
        }
//多语言文件
        new Thread(() -> executeIot("https://otadown-eu-pp.ai-ways.com/download/manage/sss/9e511a80cecb4871b5da4fd2c0457361.xml?X-Amz-Security-Token=IQoJb3JpZ2luX2VjED4aDGV1LWNlbnRyYWwtMSJHMEUCIQCBUhe3UmdR5O7WJ%2Bsk6C6qzHvBI22CiQ088hLgeDO0FQIgM%2Fxfc3gzc4y51X9TyIxvaYZiqDPbBLDgJOo5Eonp0QEqwwMI5%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARABGgw4MjAwMjg1NDY5MDUiDGlXY8MXasU8blO7oiqXA2xw6NJBBh5DwGGYdXoz9U9UR5rpijy0FsmB7u14xGH6tG1HpkrYhywD1krfrm5vlW%2FFHHLdgnSbLbWMpw9EmqEQBNEPI6BIxmz9a8XAKuv5qTV6FyEeLLBZiEeYCaK7Q9njBHDtXi9Q3gTmavfjoUHenc2AM5%2FbOzEGsIZk9Q5GuTnS3ehov2wP7aiUbEeAM354Efw4noLVtrzfBZE%2FU%2B6OmrrmBGpfg182dYxHAPbDOelQAQpVFH6pHJ3Hh%2Bgt%2FXUhM6IzcZxvkVGVNGoglpciGnsbWKoTA%2FdIMa97Nnb9J0Y6el5MUZq6PepEeGb73%2Bmz1hrMCdVpaYx75NE7n8W4DrDEqYZtvfUGSh4ArieNEp1qrH0KNbi60%2FizZdN10nIeZaD%2Fm3YssRF6NZUhdlKivkOvaxRIJbcE6TipE1dWiBsGVN2UxP5e7clsCZQ9Dt9Y7MPFnOjvZb1%2FpBeFi2BEvwQH%2F0Rg5et08iA%2FLcfE3p2QD0YkuMLTKnikNU%2BZaOLwW8yBPNY%2B9pV12SWnnDxDlvH41u6HMMPvlf8FOusBcNleVZRG2tg%2BWqhzK%2BZaLbtu5jRnd%2FElDiUwtXn61EYbwSYYv1g%2FSClIhiEucR90HeTb%2FEM234dCWO2VztlJZMN%2BcNl%2Bg4I9HKIhBOLYyCDtRsMAj%2FbWlvgIP9TiviXmjs0zRLG80C%2BclSe9Byrp47JCDQXKrn4w3XTmsHqptZgfBvtakKMXgi0wvFrEGwcKcldeKqNqCCGBqvYC4HYctrYZseXLrU9A8B23NfTTnASItc%2BBBQBQe4DQhH60P91N3PAAVUfJi6NddG82nox%2BvJt0FNh4O%2BKxtpefiyTT80YJUzrH4U9%2F46rMFw%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20201225T061835Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604800&X-Amz-Credential=ASIA353MCT5M5QXDG4F3%2F20201225%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=808243695d9d60a40d52040752076f4ddb1b851d277c59c276809ef8db26812d",
                filePath, "ef426f716033c2ffeeb79f57cdd841b4801a9d67670c11ab8f6129713b9b0e4f")).start();


//升级包文件
//        new Thread(() -> executeIot("https://otadown-eu-pp.ai-ways.com/DEV-OTA/download/manage/sss/91a41e746424451bbff2faf318cb99ff.zip?X-Amz-Security-Token=IQoJb3JpZ2luX2VjEDcaDGV1LWNlbnRyYWwtMSJHMEUCIQDaFYH7tHXmhWh%2BydDmIYKaK1XBGs9MbhWiJzkF3FQWxQIgbfG1mosjndc5kbXuhh%2FWGLtqiikMVueZR%2FUr4OXmJBEqwwMI4P%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARABGgw4MjAwMjg1NDY5MDUiDJ4HNLPWlbTMWS1X0yqXA%2B7tY%2FYN2pFx0FCgTPyyOmNn9GErNIOw7uCzIdSgzg%2BoNr4MOK0Io4u5DYNHJJILyWy49z%2BGa7qV5HlGnkPJGvqEx5HeR00L3y6qUyo7DYjc45vCefPHTplmyllE0yffoR9GvBjLufjE8tSPHYUWHceE5vq%2BwR82jN3KUrsvjwRihf1nizkCaln7Eb4coGMi%2BcoIGnh7bKCNosbSpmynGJXQTL0MRezi4OAD8VM4CgDWS3BjQwxpr%2ByKacQAWYMmkcrIYlz4aEfBogs9sml7N16fRDmsjVxWq%2FfIbXoRfa8ek0twj9pEvJRgFUhEgN3srTO8tLILJHl4YIhfkevWkmb2%2FBo68uIeVYhSgTvkYuPW4F6uAL9tw2sQQ7Y%2BQqAeELTeZUxvohvplpkuhk1RRi0t6qnhS8hski%2BGQNK32qoKw4uYz0MZipSYiYqFNDzMt5vrOqB9gk08cL5nPPtl4czou9Smlwq8%2F9%2FMLa9EgUJAIehfn5Tl78i9xXe%2BglLufkp3pjzET1fabdH81WVgvnD6j75BM2KMMK7ElP8FOusBYikCTiaSH066EyeUNNCS4v1xWknwvz8oRO15LFNV9xRN4x4a0geD9gAUbk%2Fq%2BIQloqO%2Ba%2BNGywwjKqW187xJrbINkAcl9rfXeMW28zH1Sf7Oz%2BqFg1KOZQbFCGNgjsbp%2Fyk9yDU6131OOuhND0N44pgDWju9teSifOsQjxVvPTC%2Bfkqjc8NZM4ZWnbBvL2yh1861I6YoDCvDCbV%2BFOG0MVr9xkyFNSgGTQ1aSE9sPqRvUkzG5muCnkkhPj5C7IPg3qxdIdSaKKcOXJBHSccX25Po6M44vFkqj6FaMUMRv02s8txJ3tDBQ2vzBw%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20201225T000000Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604800&X-Amz-Credential=ASIA353MCT5MSGOZBENA%2F20201225%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=0375cdecc3c28d157383c157911d4fbd1d0c6d1f73ad5406e5e6bd9102ca5395",
//                filePath,
//                "bd7098e98986cc6a9999f9d5f97a53f09a1aa7f533b48c43c23fc36682584995", 42381)).start();
    }

    public void clearLog(View view) {
        builder.setLength(0);
        tv_logcat.setText(builder);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void execute() {
        Trace.d(TAG, "MultiLanguageTask execute() start");
        //String languageFileUrl = "https://fota-down-eu.soimt.com/%2Fdownload/strategy/test/language/181d4d0122204652a6287f5d98cf1ca5.xml?AWSAccessKeyId=AKIAZEN2DFU6Q54HXFYT&Expires=1608619240&Signature=ertE2oI0TAlX1owm2HhC8jd0MKE%3D";
        //String languageFileHash = "3b64b634960af2815c52c61022e90106a17b16428b10fe8d7ef9bd2a5326d5c2";

        String languageFileUrl = "https://otadown-eu-pp.ai-ways.com/download/manage/sss/9e511a80cecb4871b5da4fd2c0457361.xml?X-Amz-Security-Token=IQoJb3JpZ2luX2VjED4aDGV1LWNlbnRyYWwtMSJHMEUCIQCBUhe3UmdR5O7WJ%2Bsk6C6qzHvBI22CiQ088hLgeDO0FQIgM%2Fxfc3gzc4y51X9TyIxvaYZiqDPbBLDgJOo5Eonp0QEqwwMI5%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARABGgw4MjAwMjg1NDY5MDUiDGlXY8MXasU8blO7oiqXA2xw6NJBBh5DwGGYdXoz9U9UR5rpijy0FsmB7u14xGH6tG1HpkrYhywD1krfrm5vlW%2FFHHLdgnSbLbWMpw9EmqEQBNEPI6BIxmz9a8XAKuv5qTV6FyEeLLBZiEeYCaK7Q9njBHDtXi9Q3gTmavfjoUHenc2AM5%2FbOzEGsIZk9Q5GuTnS3ehov2wP7aiUbEeAM354Efw4noLVtrzfBZE%2FU%2B6OmrrmBGpfg182dYxHAPbDOelQAQpVFH6pHJ3Hh%2Bgt%2FXUhM6IzcZxvkVGVNGoglpciGnsbWKoTA%2FdIMa97Nnb9J0Y6el5MUZq6PepEeGb73%2Bmz1hrMCdVpaYx75NE7n8W4DrDEqYZtvfUGSh4ArieNEp1qrH0KNbi60%2FizZdN10nIeZaD%2Fm3YssRF6NZUhdlKivkOvaxRIJbcE6TipE1dWiBsGVN2UxP5e7clsCZQ9Dt9Y7MPFnOjvZb1%2FpBeFi2BEvwQH%2F0Rg5et08iA%2FLcfE3p2QD0YkuMLTKnikNU%2BZaOLwW8yBPNY%2B9pV12SWnnDxDlvH41u6HMMPvlf8FOusBcNleVZRG2tg%2BWqhzK%2BZaLbtu5jRnd%2FElDiUwtXn61EYbwSYYv1g%2FSClIhiEucR90HeTb%2FEM234dCWO2VztlJZMN%2BcNl%2Bg4I9HKIhBOLYyCDtRsMAj%2FbWlvgIP9TiviXmjs0zRLG80C%2BclSe9Byrp47JCDQXKrn4w3XTmsHqptZgfBvtakKMXgi0wvFrEGwcKcldeKqNqCCGBqvYC4HYctrYZseXLrU9A8B23NfTTnASItc%2BBBQBQe4DQhH60P91N3PAAVUfJi6NddG82nox%2BvJt0FNh4O%2BKxtpefiyTT80YJUzrH4U9%2F46rMFw%3D%3D&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20201225T061835Z&X-Amz-SignedHeaders=host&X-Amz-Expires=604800&X-Amz-Credential=ASIA353MCT5M5QXDG4F3%2F20201225%2Feu-central-1%2Fs3%2Faws4_request&X-Amz-Signature=808243695d9d60a40d52040752076f4ddb1b851d277c59c276809ef8db26812d";
        String languageFileHash = "ef426f716033c2ffeeb79f57cdd841b4801a9d67670c11ab8f6129713b9b0e4f";
        String filePath = getExternalCacheDir().getAbsolutePath() + "/package2/multiLanguage.xml";

        print("execute url is " + languageFileUrl);
        print("execute hash is " + languageFileHash);
        print("execute filePath is " + filePath);
        File file = new File(filePath);
        if (file.isFile() || file.isDirectory()) {
            print("execute file delete----- " + file.delete());
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


    public boolean executeIot(String xmlUrl, String filePath, String languageFileHash, long fileSize) {
        Trace.d(TAG, "downloadFile() start，url:%s, filePath:%s", xmlUrl, filePath);
        com.abupdate.iot_download_libs.DownConfig.setVerifyMode(com.abupdate.iot_download_libs.verifymode.Impl.VerifySha256Impl.INSTANCE);
        com.abupdate.iot_download_libs.DownEntity downEntity = new com.abupdate.iot_download_libs.DownEntity()
                .setFilePath(filePath)
                .setVerifyCode(languageFileHash)
                .setFileSize(fileSize)
                .setUrl(xmlUrl);
        DLManager.getInstance().add(downEntity);
        final boolean[] success = {false};
        DLManager.getInstance().execute(new DownSimpleListener() {
            @Override
            public void on_finished(List<com.abupdate.iot_download_libs.DownEntity> list, List<com.abupdate.iot_download_libs.DownEntity> list1) {
                if (list.size() > 0) {
                    Trace.d(TAG, "download success");
                    print("download success");
                    success[0] = true;
                } else {
                    Trace.d(TAG, "download fail");
                    print("download fail");
                }
                super.on_finished(list, list1);
            }
        });
        return success[0];
    }

    public boolean executeIot(String xmlUrl, String filePath, String languageFileHash) {
        Trace.d(TAG, "downloadFile() start，url:%s, filePath:%s", xmlUrl, filePath);
        com.abupdate.iot_download_libs.DownConfig.setVerifyMode(com.abupdate.iot_download_libs.verifymode.Impl.VerifySha256Impl.INSTANCE);
        com.abupdate.iot_download_libs.DownEntity downEntity = new com.abupdate.iot_download_libs.DownEntity()
                .setFilePath(filePath)
                .setVerifyCode(languageFileHash)
                .setUrl(xmlUrl);
        DLManager.getInstance().add(downEntity);
        final boolean[] success = {false};
        DLManager.getInstance().execute(new DownSimpleListener() {
            @Override
            public void on_finished(List<com.abupdate.iot_download_libs.DownEntity> list, List<com.abupdate.iot_download_libs.DownEntity> list1) {
                if (list.size() > 0) {
                    Trace.d(TAG, "download success");
                    print("download success");
                    success[0] = true;
                } else {
                    Trace.d(TAG, "download fail");
                    print("download fail");
                }
                super.on_finished(list, list1);
            }
        });
        return success[0];
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

    public void crtDownload(View view) {
        Trace.i(TAG, "crtDownload()");
        String crtFilePath = getDefaultFolder() + File.separator + "crt" + File.separator;
        String localFilePath = getDefaultFolder() + File.separator + "crtdownload" + File.separator;
        Trace.i(TAG, "crtDownload() crtFilePath =" + crtFilePath);
        Trace.i(TAG, "crtDownload() localFilePath =" + localFilePath);
        Trace.i(TAG, "crtDownload() FileUtil createOrExistsFile crtFilePath  result =" + FileUtil.createOrExistsFile(crtFilePath));
        Trace.i(TAG, "crtDownload() FileUtil createOrExistsFile localFilePath result =" + FileUtil.createOrExistsFile(localFilePath));
        DownloadUtil.executeDownload(crtFilePath + "cdn.crt", "https://otadown.ai-ways.com/PRD-OTA/download/manage/oss/f89a91a2e9984bbda38f6896254294cb.bin?Expires=1609891200&OSSAccessKeyId=LTAIIlxujGXchowU&Signature=XDVSVUNa%2Bsqqd8e6rHz9LPFjwG0%3D",
                localFilePath, 262145, "4c4136c340f32afe8a3670e585e94401586c7e8e751481eb438085d36a0c6ae9");
    }


    private static String getDefaultFolder() {
        Context context = ContextVal.getContext();
        String path;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //sd卡挂载
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //大于6.0 存放于Android/data/data/包名/cache 此路径不需要申请权限
                if (context.getExternalCacheDir() == null) {
                    path = context.getCacheDir().getAbsolutePath();
                } else {
                    path = context.getExternalCacheDir().getAbsolutePath();
                }
            } else {
                //小于6.0 存放于内置存储卡根目录
                path = Environment.getExternalStorageDirectory().getAbsolutePath();
            }
        } else {
            //sd卡不可用
            path = context.getCacheDir().getAbsolutePath();
        }
        return path;
    }
}