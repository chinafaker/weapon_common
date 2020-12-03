package com.hhb.appdemo;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hhb.common.Trace;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Trace.i(TAG, "onCreate()");
    }
}