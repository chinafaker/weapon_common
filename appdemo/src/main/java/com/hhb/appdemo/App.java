package com.hhb.appdemo;

import android.app.Application;
import android.os.Trace;


/**
 * @author hhb
 * @date 2020/11/20 14:23
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Trace.setLevel(Trace.DEBUG);
        Trace.setShowPosition(true);
    }
}
