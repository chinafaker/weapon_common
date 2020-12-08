package com.abupdate.packer

import org.gradle.api.Project

class SendEmailConfigExtensionHandler {
    static void sendEmail(Project project, def autoPackExtension) {
        Log.D("sendEmail() start")
        def sendEmailConfig = autoPackExtension.sendEmailConfig
        if (sendEmailConfig == null) {
            Log.D("sendEmail() sendEmailConfig is null")
            return
        }
        String projectName = sendEmailConfig.projectName
        String version = sendEmailConfig.version
        String releaseNote = sendEmailConfig.releaseNote
        Log.D("projectName:${projectName},version:${version},releaseNote:${releaseNote}")

        Log.D("sendEmail() end")
    }
}