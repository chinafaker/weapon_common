package com.abupdate.packer

class PackOutputConfig {

    /**
     * 输出包目录
     */
    String outputDir

    /**
     * 是否压缩
     */
    boolean compress
    /**
     * 是否发送邮件
     */
    boolean sendEmail

    /**
     * 其他文件
     */
    String[] otherFiles

    /**
     * 重命名APK文件
     */
    Closure renameApkFile

    /**
     * 文件夹名
     */
    Closure packDirName

}