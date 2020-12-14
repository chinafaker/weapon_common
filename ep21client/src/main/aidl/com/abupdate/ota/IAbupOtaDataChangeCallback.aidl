// IAbupOtaDataChangeCallback.aidl
package com.abupdate.ota;

// Declare any non-default types here with import statements

interface IAbupOtaDataChangeCallback {

    void onNewVersionChange(boolean exist);

    void onAvnMpuVersionChange(String version);

    void onTboxVersionChange(String version);
}