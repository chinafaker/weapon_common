package com.hhb.aidlservertest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
    }
}