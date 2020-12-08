package com.abupdate.packer

import org.gradle.api.Project

class UploadFtpConfigExtensionHandler {

    static void uploadFtp(Project project, def autoPackExtension) {
        Log.D("uploadFtp() start")
        def uploadFtpConfig = autoPackExtension.uploadFtpConfig
        if (uploadFtpConfig == null) {
            Log.D("uploadFtp() uploadFtpConfig is null")
            return
        }
        String serverIp = uploadFtpConfig.serverIp
        String userName = uploadFtpConfig.userName
        String passWord = uploadFtpConfig.passWord
        String localPath = uploadFtpConfig.localPath
        String uploadFtpPath = uploadFtpConfig.uploadFtpPath
        Log.D("serverIp:${serverIp},userName:${userName},passWord:${passWord},localPath:${localPath},uploadFtpPath:${uploadFtpPath}")
        
        Log.D("uploadFtp() end")
    }
}