package com.hhb.common.interfaces;

import com.hhb.common.Dispatcher;
import com.hhb.common.Trace;

public abstract class NamedRunnable implements Runnable {
    protected final String name;

    public NamedRunnable(String name) {
        this.name = name;
    }

    @Override
    public final void run() {
        String oldName = Thread.currentThread().getName();
        Thread.currentThread().setName(name);
        try {
            execute();
        } catch (Exception e) {
            Trace.e("NamedCallable/" + name, "call() e = " + e);
            e.printStackTrace();
        } finally {
            Thread.currentThread().setName(oldName);
            finished();
        }
    }

    protected abstract void execute();

    private void finished() {
        Dispatcher.with().finished(this);
    }
}