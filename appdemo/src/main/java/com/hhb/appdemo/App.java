package com.hhb.appdemo;

import android.app.Application;

import com.hhb.common.Trace;
import com.tencent.mmkv.MMKV;

import java.util.Arrays;


/**
 * @author hhb
 * @date 2020/11/20 14:23
 */
public class App extends Application {
    private static final String TAG = "App";

    @Override
    public void onCreate() {
        super.onCreate();
        initTrace();
        initMMKV();
        testMMKV();
    }

    private void initMMKV() {
        String rootDir = MMKV.initialize(this);
        Trace.i(TAG, "mmkv root is %s", rootDir);
    }

    private void initTrace() {
        Trace.setLevel(Trace.DEBUG);
        Trace.setShowPosition(true);
    }

    private void testMMKV() {
        MMKV kv = MMKV.defaultMMKV();
        kv.encode("bool", true);
        Trace.i(TAG, "testMMKV() bool is " + kv.decodeBool("bool"));

        kv.encode("int", Integer.MIN_VALUE);
        Trace.i(TAG, "testMMKV() int is " + kv.decodeBool("int"));

        kv.encode("string", "Hello from mmkv");
        Trace.i(TAG, "testMMKV() string is " + kv.decodeBool("string"));

        boolean hasBool = kv.containsKey("bool");
        Trace.i(TAG, "testMMKV() hasBool is " + hasBool);

        kv.removeValueForKey("bool");
        Trace.i(TAG, "testMMKV() bool is " + kv.decodeBool("bool"));

        hasBool = kv.containsKey("bool");
        Trace.i(TAG, "testMMKV() hasBool is " + hasBool);
    }
}
