package com.abupdate.packer

import org.gradle.api.Project

class UploadFtpConfigExtensionHandler {

    static void uploadFtp(Project project, def autoPackExtension) {
        Log.D("uploadFtp config start")
        def uploadFtpConfig = autoPackExtension.uploadFtpConfig
        if (uploadFtpConfig == null) {
            Log.D("uploadFtp() uploadFtpConfig is null")
            return
        }
        AutoPackTask autoPackTask = project.tasks.findByName(AutoPackTask.taskName())
        autoPackTask.uploadFtpConfig = uploadFtpConfig
        Log.D("uploadFtp config end")
    }
}