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
        String serverPort = uploadFtpConfig.serverPort
        String userName = uploadFtpConfig.userName
        String passWord = uploadFtpConfig.passWord
        String uploadFtpPath = uploadFtpConfig.uploadFtpPath
        String[] localPaths = uploadFtpConfig.localPaths
        Log.D("serverIp:${serverIp},serverPort:${serverPort},userName:${userName},passWord:${passWord},uploadFtpPath:${uploadFtpPath},localPaths:${localPaths}")
        FtpUtil.uploadFile(serverIp, serverPort, userName, passWord, uploadFtpPath, localPaths)
        Log.D("uploadFtp() end")
    }
}