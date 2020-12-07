package com.hhb.common;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

/**
 * https://www.jianshu.com/p/6f6d1352fb43
 */
public class CommonContextProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        ContextVal.setContext(getContext());
        ContextVal.getContext().sendBroadcast(new Intent(ContextVal.ACTION_APP_BOOT_COMPLETED));
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
