package com.hhb.aidl;

// Declare any non-default types here with import statements
import com.hhb.aidl.AECU;
interface IAECUCallback {
       void onSuccess(in AECU ecu);

       void onFailed(int error);
}