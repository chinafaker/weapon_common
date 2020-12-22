package com.hhb.aidlservertest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.abupdate.common_download.DownManager;
import com.abupdate.common_download.DownTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startService(View view) {
        Log.d("MainActivity", "startService()");
        // 开启服务
        Intent intent = new Intent(this, OtaService.class);
        intent.setPackage(getPackageName());
        startService(intent);
        new Thread(new Runnable() {
            /**
             * When an object implementing interface <code>Runnable</code> is used
             * to create a thread, starting the thread causes the object's
             * <code>run</code> method to be called in that separately executing
             * thread.
             * <p>
             * The general contract of the method <code>run</code> is that it may
             * take any action whatsoever.
             *
             * @see Thread#run()
             */
            @Override
            public void run() {
                DownManager.exec(new DownTask());
            }
        }).start();

//        DLManager.getInstance().execAsync(new DownSimpleListener() {
//            @Override
//            public void on_start() {
//                super.on_start();
//            }
//
//            @Override
//            public void on_finished(List<DownEntity> list, List<DownEntity> list1) {
//                super.on_finished(list, list1);
//            }
//
//            @Override
//            public void on_success(DownEntity downEntity) {
//                super.on_success(downEntity);
//            }
//
//            @Override
//            public void on_failed(DownEntity downEntity) {
//                super.on_failed(downEntity);
//            }
//
//            @Override
//            public void on_manual_cancel() {
//                super.on_manual_cancel();
//            }
//
//            @Override
//            public void on_all_progress(int i, long l, long l1) {
//                super.on_all_progress(i, l, l1);
//            }
//
//            @Override
//            public void on_progress(DownEntity downEntity, int i, long l, long l1) {
//                super.on_progress(downEntity, i, l, l1);
//            }
//        });
    }
}