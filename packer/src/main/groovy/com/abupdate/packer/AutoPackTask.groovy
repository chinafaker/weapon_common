package com.abupdate.packer

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public class AutoPackTask extends DefaultTask {

    String outputDir
    String packDirName
    String emailReleaseNoteFile
    boolean compress
    boolean sendEmail
    List<ApkRenameDate> srcPathList

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
            Log.D("sendEmail end:${sendEmail}")
            Log.D("emailReleaseNoteFile end:${emailReleaseNoteFile}")
            if (sendEmail) {
                //TODO 进行sendEmail操作
                Log.D("========sendEmail  begin=========")
                Log.D("========sendEmail  emailReleaseNoteFile  path is " + emailReleaseNoteFile)
                Log.D("emailReleaseNoteFile content is \n" + FileUtils.readFile2String(emailReleaseNoteFile))
                Log.D("========createFile  start=========")
                FileUtils.createFile(FileUtils.readFile2String(emailReleaseNoteFile), new File(emailReleaseNoteFile).getParent() + "/11111.txt")
                Log.D("========createFile  end=========")
                Log.D("========sendEmail  end=========")
            }
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