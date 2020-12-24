package com.abupdate.packer

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

class AutoPacker implements Plugin<Project> {
    Project project
    PackOutputConfigExtensionHandler packOutputConfigExtensionHandler = new PackOutputConfigExtensionHandler()
    TaskConfigExtensionHandler taskConfigExtensionHandler = new TaskConfigExtensionHandler()
    UploadFtpConfigExtensionHandler uploadFtpConfigExtensionHandler = new UploadFtpConfigExtensionHandler()
    EmailTemplateConfigExtensionHandler emailTemplateConfigExtensionHandler = new EmailTemplateConfigExtensionHandler()

    @Override
    void apply(Project project) {
        Log.D("apply project start..")
        this.project = project
        project.extensions.create('autoPack', AutoPackExtension)
        project.autoPack.extensions.create('packOutputConfig', PackOutputConfig)
        project.autoPack.extensions.create('taskConfig', TaskConfig)
        project.autoPack.extensions.create('uploadFtpConfig', UploadFtpConfig)
        project.autoPack.extensions.create('emailTemplateConfig', EmailTemplateConfig)
        //Task AutoPack
        AutoPackTask autoPackTask = project.tasks.create(AutoPackTask.taskName(), AutoPackTask.class)
        Task buildTask = project.tasks.getByName('assemble')
        autoPackTask.setGroup('AutoPack')
        autoPackTask.dependsOn(buildTask)
        autoPackTask.setDescription('Automated packaging task')

        project.afterEvaluate {
            Log.D("afterEvaluate")
            if (hasAutoPackTask()) {
                Log.D("hasAutoPackTask")
                def autoPackExtension = project['autoPack']
                if (autoPackExtension == null) {
                    throw new GradleException('Please config the autoPack dsl')
                }

                def taskConfigExtension = autoPackExtension.taskConfig
                if (taskConfigExtension == null) {
                    throw new GradleException('Please config taskConfigExtension')
                } else {
                    taskConfigExtensionHandler.applyTaskConfig(project, taskConfigExtension)
                }

                def packOutputConfig = autoPackExtension.packOutputConfig
                if (packOutputConfig == null) {
                    throw new GradleException('Please config packOutputConfig')
                } else {
                    packOutputConfigExtensionHandler.renamePackFile(project, autoPackExtension)
                }
                //上传至ftp
                def uploadFtpConfig = autoPackExtension.uploadFtpConfig
                if (uploadFtpConfig == null) {
                    throw new GradleException('Please config uploadFtpConfig')
                } else {
                    uploadFtpConfigExtensionHandler.uploadFtp(project, autoPackExtension)
                }
                //配置邮件模板
                def emailTemplateConfig = autoPackExtension.emailTemplateConfig
                if (emailTemplateConfig == null) {
                    throw new GradleException('Please config emailTemplateConfig')
                } else {
                    emailTemplateConfigExtensionHandler.templateConfig(project, autoPackExtension)
                }
            } else {
                Log.D("not has AutoPackTask")
            }
            Log.D("apply project end..")
        }
    }

    boolean hasAutoPackTask() {
        def taskNames = project.gradle.startParameter.taskNames
        List<String> targetTaskNames = [AutoPackTask.taskName(),
                                        AutoPackTask.taskShortName(),
                                        String.format(":%s:%s", project.name, AutoPackTask.taskName()),
                                        String.format(":%s:%s", project.name, AutoPackTask.taskShortName())]
        def hasAutoPackTask = false
        taskNames.each { name ->
            if (targetTaskNames.contains(name)) {
                hasAutoPackTask = true
            }
        }
        return hasAutoPackTask
    }
}