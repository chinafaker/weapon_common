package com.abupdate.packer

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public class AutoPackTask extends DefaultTask {

    String outputDir
    String packDirName
    boolean compress
    List<ApkRenameDate> srcPathList
    // UploadFtp
    UploadFtpConfig uploadFtpConfig
    //EmailTemplateConfig
    EmailTemplateConfig emailTemplateConfig

    @TaskAction
    void pack() {
        Log.D("AutoPackTask execute start")
        Log.D("outputDir:${outputDir}")
        Log.D("packDirName:${packDirName}")
        Log.D("srcPathList:${srcPathList}")
        if (outputDir != null && "" != outputDir) {
            File outputDirFile = new File(outputDir)
            if (outputDirFile.getParentFile().getName() == "build") {
                //只有在build下的目录才能被删除，避免因为传入错误参数导致删除掉代码文件
                Log.D("delete files in ${outputDir}")
                FileUtils.delFileInDir(outputDirFile.getAbsolutePath())
            }
            File parentFile = new File(outputDir + File.separator + packDirName)
            parentFile.mkdirs()
            Log.D("========begin copy=========")
            srcPathList.each { apkRenameDate ->
                File srcFile = new File(apkRenameDate.srcFilePath)
                if (srcFile.exists()) {
                    File targetFile = new File(parentFile, apkRenameDate.targetFileName)
                    targetFile.withOutputStream { os ->
                        srcFile.withInputStream { ins ->
                            os << ins
                        }
                    }
                    Log.D(srcFile.getAbsolutePath() + "==>" + targetFile.getAbsolutePath())
                }
            }
            Log.D("========end copy=========")

            if ("compile" == packDirName) {
                //默认的文件夹名，重命名
                def renameDir = renameCompileDir(outputDir + File.separator + packDirName)
                Log.D("will rename to ${renameDir}")
                if (renameDir != null && renameDir != "") {
                    boolean rename = new File(outputDir + File.separator + packDirName).renameTo(new File(outputDir + File.separator + renameDir))
                    Log.D("rename success? ${rename}")
                    if (rename) {
                        packDirName = renameDir
                    }
                }
            }
            if (compress) {
                Log.D("========begin compress=========")
                File packFileDir = new File(outputDir + File.separator + packDirName)
                File[] compressFiles = packFileDir.listFiles()
                boolean zipFile = Zip.zipFiles(compressFiles, new File(packFileDir.getParent() + File.separator + packFileDir.getName() + ".zip").getAbsolutePath())
                Log.D("compress end:${zipFile}")
            }
            if (uploadFtpConfig.uploadFtp) {
                Log.D("========uploadFtp start========")
                FtpUtil.uploadFile(uploadFtpConfig.serverIp, uploadFtpConfig.serverPort, uploadFtpConfig.userName, uploadFtpConfig.passWord, uploadFtpConfig.uploadFtpPath, uploadFtpConfig.localPaths)
                Log.D("========uploadFtp end========")
            } else {
                Log.D("========not config upload Ftp=========")
            }
            if (emailTemplateConfig.templateConfig) {
                Log.D("========make email template start=========")
                String releaseNote = FileUtils.readFile2String(emailTemplateConfig.releaseNoteLocalPath)
                int strStartIndex = releaseNote.indexOf("新版说明：")
                int strEndIndex = releaseNote.indexOf("更新履历：")
                if (strStartIndex < 0) {
                    Log.D("字符串 新版说明: 不存在, 无法截取更新内容")
                    return
                }
                if (strEndIndex < 0) {
                    Log.D("字符串 更新履历: 不存在, 无法截取更新内容")
                    return
                }
                releaseNote = releaseNote.substring(strStartIndex, strEndIndex).substring("新版说明：".length()).replaceAll("\n", "\n   ")
                StringBuffer stringBuffer = new StringBuffer()
                stringBuffer.append("Hi, xx:")
                        .append("\n\n")
                        .append("   ${emailTemplateConfig.projectName}项目的APK文件已上传FTP，版本号：V${project.android.defaultConfig.versionName}，请安排进行测试。\n\n")
                        .append("   更新内容:")
                        .append(releaseNote)
                FileUtils.createFile(stringBuffer.toString(), parentFile.path + "/邮件模板.txt")
                Log.D("========make email template end=========")
            } else {
                Log.D("========not config make email template=========")
            }
            Log.D("AutoPackTask execute end")
        } else {
            Log.D("========outputDir is invalid========")
        }
    }

    String renameCompileDir(String path) {
        def dir = new File(path)
        def files = dir.listFiles(new FilenameFilter() {
            @Override
            boolean accept(File fDir, String name) {
                return name.endsWith(".apk")
            }
        })
        if (files.length == 0) {
            Log.D("apk file is not exist")
            return ""
        }
        if (files.length == 1) {
            return files[0].getName().replace(".apk", "")
        }
        //找出文件名中共同的部分
        Map<String, Integer> map = new HashMap<>()
        StringBuilder builder = new StringBuilder()
        def commonSplit = files[0].getName().split("_")
        for (file in files) {
            def fileName = file.getName()
            //将project、variant、version等切分出来
            def splitFirst = fileName.split("_")
            String variant = splitFirst[1]
//            Log.D("File name:" + fileName)
            def splitSecond = variant.split("(?<!^)(?=[A-Z])")
            for (s in splitSecond) {
//                Log.D("split:" + s)
                int count = map.get(s) == null ? 0 : map.get(s)
                map.put(s, ++count)
            }
        }
        for (stringIntegerEntry in map.entrySet()) {
            String key = stringIntegerEntry.getKey()
            int value = stringIntegerEntry.getValue()
            if (value == files.length) {
                builder.append(key)
            }
        }
        commonSplit[1] = builder.toString()

        StringBuilder builder1 = new StringBuilder()
        for (s in commonSplit) {
            if (s == null || s.length() == 0) {
                continue
            }
            if (builder1.toString().length() != 0) {
                builder1.append("_")
            }
            builder1.append(s)
        }
        Log.D("rename dir:" + builder1.toString())
        return builder1.toString().replace(".apk", "")
    }

    static String taskName() {
        return "autoPack"
    }

    static String taskShortName() {
        return "aP"
    }
}