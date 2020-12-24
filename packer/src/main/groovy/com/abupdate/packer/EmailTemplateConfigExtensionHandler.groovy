package com.abupdate.packer

import org.gradle.api.GradleException
import org.gradle.api.Project

class EmailTemplateConfigExtensionHandler {

    static void templateConfig(Project project, def autoPackExtension) {
        Log.D("templateConfig config start")
        def emailTemplateConfig = autoPackExtension.emailTemplateConfig
        if (emailTemplateConfig == null) {
            Log.D("templateConfig() templateConfig is null")
            return
        }
        if (emailTemplateConfig.templateConfig) {
            String projectName = emailTemplateConfig.projectName
            if (projectName.isEmpty()) {
                throw new GradleException('email template config projectName is error , please check')
            }
            File file = new File(emailTemplateConfig.releaseNoteLocalPath)
            if (!file.exists() || !file.isFile()) {
                throw new GradleException('email template config releaseNoteLocalPath is error, please check')
            }
            String releaseNote = FileUtils.readFile2String(emailTemplateConfig.releaseNoteLocalPath)
            if (!releaseNote.contains("新版说明：")) {
                throw new GradleException('local release note not contains New version description, please check')
            }
            if (!releaseNote.contains("更新履历：")) {
                throw new GradleException('local release note not contains Update resume, please check')
            }
        }
        AutoPackTask autoPackTask = project.tasks.findByName(AutoPackTask.taskName())
        autoPackTask.emailTemplateConfig = emailTemplateConfig
        Log.D("templateConfig config end")
    }
}