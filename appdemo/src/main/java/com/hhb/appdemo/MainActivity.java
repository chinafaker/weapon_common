package com.hhb.appdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Trace;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Trace.i(TAG, "onCreate()");
    }
}