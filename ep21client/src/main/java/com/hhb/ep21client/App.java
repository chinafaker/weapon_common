package com.hhb.ep21client;

import android.app.Application;

import com.abupdate.common.Trace;


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
    }

    private void initTrace() {
        Trace.setLevel(Trace.DEBUG);
        Trace.setShowPosition(true);
    }

}
