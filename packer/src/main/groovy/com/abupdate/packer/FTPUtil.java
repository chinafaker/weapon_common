package com.abupdate.packer;


import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUtil {

    public static void uploadFile(String host, String username, String password, String remotePath, String remoteFileName, String localPath, String localFileName) {
        Log.D(">>>>>>>>FTP-->uploadFile--login start >>>>>>>>>>>>>");
        FTPClient ftpClient = new FTPClient();
        Log.D(">>>>>>>>FTP-->uploadFile--login start 1>>>>>>>>>>>>>");
        FileInputStream inputStream = null;
        Log.D(">>>>>>>>FTP-->uploadFile--login start 2>>>>>>>>>>>>>");
        try {
            ftpClient.connect(host);
            Log.D(">>>>>>>>FTP-->uploadFile--login start 3>>>>>>>>>>>>>");
            //设置成被动FTP模式
            ftpClient.enterLocalPassiveMode();
            Log.D(">>>>>>>>FTP-->uploadFile--login start 4>>>>>>>>>>>>>");
            boolean login = ftpClient.login(username, password);
            if (login) {
                Log.D(">>>>>>>>FTP-->uploadFile--登录成功>>>>>>>>>>>>>");
            } else {
                Log.D(">>>>>>>>FTP-->uploadFile--登录失败>>>>>>>>>>>>>");
                throw new RuntimeException("FTP登陆失败,请检查用户信息！");
            }
            //切换文件路径
            ftpClient.makeDirectory(remotePath);
            ftpClient.changeWorkingDirectory(remotePath);
            inputStream = new FileInputStream(new File(localPath + localFileName));
            //可上传多文件
            boolean isUpload = ftpClient.storeFile(remoteFileName, inputStream);
            if (isUpload) {
                Log.D(">>>>>>>>FTP-->uploadFile--文件上传成功!");
            } else {
                Log.D(">>>>>>>>FTP-->uploadFile--文件上传失败!");
                throw new RuntimeException("文件上传失败!");
            }
        } catch (IOException e) {
            Log.D(">>>>>>>>FTP-->uploadFile--IOException e" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}