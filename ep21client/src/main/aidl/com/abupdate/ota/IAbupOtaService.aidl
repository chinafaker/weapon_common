// IAbupOtaService.aidl
package com.abupdate.ota;

// Declare any non-default types here with import statements
import com.abupdate.ota.IAbupOtaDataChangeCallback;
interface IAbupOtaService {

    boolean existNewVersion();

    String getAvnMpuVersion();

    String getTboxMpuVersion();

    String getTboxMcuVersion();

    String getTboxVersion();

    void  registerAbupOtaDataChangeCallback(IAbupOtaDataChangeCallback callback);
}
