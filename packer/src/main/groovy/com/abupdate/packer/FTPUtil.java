package com.abupdate.packer;


import org.apache.commons.net.ftp.FTPClient;
import org.gradle.api.GradleException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUtil {
    /**
     * @param host
     * @param port
     * @param username
     * @param password
     * @param uploadFtpPath
     * @param localPaths
     */
    public static void uploadFile(String host, String port, String username, String password, String uploadFtpPath, String[] localPaths) {
        Log.D("uploadFile start");
        if (null == host || host.isEmpty()) {
            throw new GradleException("uploadFile ftp  serverip config can not be null");
        }
        try {
            if (null != port || !port.isEmpty()) {
                Integer.parseInt(port);
            }
        } catch (NumberFormatException e) {
            throw new GradleException("uploadFile ftp  serverPort config can not be null");
        }
        if (null == username || username.isEmpty()) {
            throw new GradleException("uploadFile ftp  username config can not be null");
        }
        if (null == password || password.isEmpty()) {
            throw new GradleException("uploadFile ftp  password config can not be null");
        }
        if (null == uploadFtpPath || uploadFtpPath.isEmpty()) {
            throw new GradleException("uploadFile ftp  uploadFtpPath config can not be null");
        }
        if (null == localPaths || localPaths.length < 1) {
            throw new GradleException("uploadFile ftp  localPaths config can not be null");
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
                throw new GradleException("uploadFile login failed,Please check your account or password");
            }
            //创建文件夹
            boolean makeDirectory = ftpClient.makeDirectory(uploadFtpPath);
            Log.D("uploadFile makeDirectory is " + makeDirectory);
            //切换文件路径
            boolean changeWorking = ftpClient.changeWorkingDirectory(uploadFtpPath);
            Log.D("uploadFile changeWorking is " + changeWorking);

            Log.D("uploadFile  localPaths length is " + localPaths.length);
            for (int index = 0; index < localPaths.length; index++) {
                String localPath = localPaths[index];
                Log.D("uploadFile  localPath is " + localPath);
                File file = new File(localPath);
                if (file.exists()) {
                    inputStream = new FileInputStream(file);
                    String remote = uploadFtpPath + "/" + file.getName();
                    boolean isUpload = ftpClient.storeFile(remote, inputStream);
                    if (isUpload) {
                        Log.D("uploadFile  upload file success");
                    } else {
                        Log.D("uploadFile  upload file failed");
                    }
                    inputStream.close();
                } else {
                    throw new GradleException("uploadFile can not find local file:" + localPath + ",please check the config");
                }
            }
        } catch (IOException e) {
            Log.D("uploadFile IOException e" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                ftpClient.disconnect();
                Log.D("uploadFile finally ftpClient disconnect");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}