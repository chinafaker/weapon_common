package com.abupdate.packer

import org.gradle.api.GradleException
import org.gradle.api.Project

class PackOutputConfigExtensionHandler {
    static void renamePackFile(Project project, def autoPackExtension) {
        Log.D("renamePackFile() start")
        def packOutputConfig = autoPackExtension.packOutputConfig
        if (packOutputConfig == null) {
            Log.D("renamePackFile() packOutputConfig is null")
            return
        }
        String outputDir = packOutputConfig.outputDir
        boolean compress = packOutputConfig.compress
        //是否发邮件
        boolean sendEmail = packOutputConfig.sendEmail
        String emailReleaseNoteFile = packOutputConfig.emailReleaseNoteFile
        String[] otherFiles = packOutputConfig.otherFiles
        Closure renameApkFile = packOutputConfig.renameApkFile
        Closure packDirName = packOutputConfig.packDirName
        Log.D("outputDir:${outputDir},compress:${compress},sendEmail:${sendEmail},emailReleaseNoteFile:${emailReleaseNoteFile}")
        Log.D("other pack files:" + otherFiles)

        if (outputDir == null) {
            throw new GradleException('AutoPack plugin require outputDir')
            return
        }
        def variants
        if (project.plugins.hasPlugin('com.android.application')) {
            variants = project.android.applicationVariants
        } else if (project.plugins.hasPlugin('com.android.library')) {
            variants = project.android.libraryVariants
        } else {
            throw new GradleException('Android Application or Library plugin required')
        }

        def taskConfigExtension = autoPackExtension.taskConfig
        String[] disableProductFlavors = taskConfigExtension.disableProductFlavors as String[]
        String[] disableBuildTypes = taskConfigExtension.disableBuildTypes as String[]
        List<ApkRenameDate> srcPathList = []

        variants.all { variant ->
            String flavorName = variant.flavorName
            if (project.plugins.hasPlugin('com.android.library')
                    && (flavorName == null || '' == flavorName)) {
                return
            }
            variant.outputs.all { output ->
                ApkRenameDate apkRenameDate = new ApkRenameDate()
                apkRenameDate.srcFilePath = output.outputFile.getAbsolutePath()
                apkRenameDate.targetFileName = output.outputFile.getName()
                if (renameApkFile != null) {
                    apkRenameDate.targetFileName = renameApkFile(project, variant)
                }
                //过滤掉不需要打包的APK
                if (!containInArray(apkRenameDate.targetFileName, disableProductFlavors) &&
                        !containInArray(apkRenameDate.targetFileName, disableBuildTypes)) {
                    srcPathList.add(apkRenameDate)
                }
            }
        }

        //添加打包其他的文件
        if (otherFiles != null && otherFiles.length > 0) {
            for (otherFile in otherFiles) {
                if (!new File(otherFile).exists()) {
                    Log.D("${otherFile} is not exist")
                } else {
                    ApkRenameDate apkRenameDate = new ApkRenameDate()
                    apkRenameDate.srcFilePath = otherFile
                    apkRenameDate.targetFileName = new File(otherFile).getName()
                    srcPathList.add(apkRenameDate)
                }
            }
        }

        AutoPackTask autoPackTask = project.tasks.findByName(AutoPackTask.taskName())
        if (packDirName != null) {
            autoPackTask.packDirName = packDirName(project)
        }
        if (autoPackTask.packDirName == null || "" == autoPackTask.packDirName) {
//            ApkRenameDate data = srcPathList[0]
//            if (data.targetFileName.contains(".apk")) {
//                autoPackTask.packDirName = data.targetFileName.replace(".apk", "")
//            }
            autoPackTask.packDirName = "compile"
        }
        autoPackTask.compress = compress
        autoPackTask.sendEmail = sendEmail
        autoPackTask.emailReleaseNoteFile = emailReleaseNoteFile
        autoPackTask.outputDir = outputDir
        autoPackTask.srcPathList = srcPathList
    }

    static boolean containInArray(String apkName, String[] array) {
        for (key in array) {
            key = key.toLowerCase()
//            println(key)
            if (apkName.contains(key)) {
                return true
            }
        }
        return false
    }
}