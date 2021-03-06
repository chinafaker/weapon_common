package com.abupdate.packer

class UploadFtpConfig {

    /**
     * 是否上传 uploadFtp
     */
    boolean uploadFtp
    /**
     * 服务器 ip
     */
    String serverIp

    /**
     * 服务器 port
     */
    String serverPort

    /**
     * 账号
     */
    String userName

    /**
     * 密码
     */
    String passWord


    /**
     * 上传ftp目录
     */
    String uploadFtpPath

    /**
     * 上传文件本地路径
     */
    String[] localPaths
}