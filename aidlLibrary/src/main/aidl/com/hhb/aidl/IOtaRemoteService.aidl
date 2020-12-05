package com.hhb.aidl;

// Declare any non-default types here with import statements
import com.hhb.aidl.IAECUCallback;
interface IOtaRemoteService {
       void getEcuInfo(String ecuDID, String ecuSwID,IAECUCallback callback);
}