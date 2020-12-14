package com.abupdate.packer;


import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUtil {
    /**
     * @param host
     * @param port
     * @param username
     * @param password
     * @param remotePath
     * @param remoteFileName
     * @param localPath
     */
    public static void uploadFile(String host, String port, String username, String password, String remotePath, String remoteFileName, String localPath) {
        Log.D("uploadFile start");
        if (null == host || host.isEmpty()) {
            Log.D("Ftp上传主机ip不能为空，请重新配置");
            return;
        }
        try {
            if (null != port || !port.isEmpty()) {
                int pt = Integer.parseInt(port);
                Log.D("uploadFile port is " + pt);
            }
        } catch (NumberFormatException e) {
            Log.D("端口号非法，请检查相关配置!");
            return;
        }
        if (null == remotePath || remotePath.isEmpty()) {
            Log.D("Ftp上传路径不能为空，请检查相关配置路径！");
            return;
        }
        if (null == username || username.isEmpty()) {
            Log.D("Ftp登录账号不能为空，请检查相关配置路径！");
            return;
        }
        if (null == password || password.isEmpty()) {
            Log.D("Ftp登录密码不能为空，请检查相关配置路径！");
            return;
        }
        if (null == remoteFileName || remoteFileName.isEmpty()) {
            Log.D("Ftp上传名称不能为空，请检查相关配置路径！");
            return;
        }
        if (null == localPath || localPath.isEmpty()) {
            Log.D("Ftp本地路径不能为空，请检查相关配置路径！");
            return;
        }
        File file = new File(localPath);
        if (!file.exists()) {
            Log.D("找不到本地文件，请检查相关配置路径!");
            return;
        }
        FTPClient ftpClient = new FTPClient();
        FileInputStream inputStream = null;
        try {
            if (port.isEmpty()) {
                ftpClient.connect(host);
            } else {
                ftpClient.connect(host, Integer.parseInt(port));
            }
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //设置成被动FTP模式
            ftpClient.enterLocalPassiveMode();
            boolean result = ftpClient.login(username, password);
            if (result) {
                Log.D("uploadFile login success ");
            } else {
                Log.D("uploadFile login failed ");
                return;
            }
            //创建文件夹
            boolean makeDirectory = ftpClient.makeDirectory(remotePath);
            Log.D("uploadFile makeDirectory is " + makeDirectory);
            //切换文件路径
            boolean changeWorking = ftpClient.changeWorkingDirectory(remotePath);
            Log.D("uploadFile changeWorking is " + changeWorking);

            inputStream = new FileInputStream(file);
            String remote = remotePath + remoteFileName;
            Log.D("uploadFile  remote is " + remote);
            boolean isUpload = ftpClient.storeFile(remote, inputStream);
            if (isUpload) {
                Log.D("uploadFile  upload file success");
            } else {
                Log.D("uploadFile  upload file failed");
            }
        } catch (IOException e) {
            Log.D("uploadFile IOException e" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                Log.D("uploadFile finally ftpClient disconnect");
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}