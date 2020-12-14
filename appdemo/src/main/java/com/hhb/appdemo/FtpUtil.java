package com.hhb.appdemo;


import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUtil {
    private static final String TAG = "FtpUtil";

    public static void uploadFile(String host, String username, String password, String remotePath, String remoteFileName, String localPath) {
        Log.i(TAG, "uploadFile login start ");
        FTPClient ftpClient = new FTPClient();
        FileInputStream inputStream = null;
        try {
            //ftpClient.connect(host);
            ftpClient.connect(host,21);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //设置成被动FTP模式
            ftpClient.enterLocalPassiveMode();
            boolean result = ftpClient.login(username, password);
            if (result) {
                Log.i(TAG, "uploadFile login success ");
            } else {
                Log.i(TAG, "uploadFile login failed ");
                throw new Exception("FTP登陆失败,请检查用户信息！");
            }
            //创建文件夹
            boolean makeDirectory = ftpClient.makeDirectory(remotePath);
            Log.i(TAG, "uploadFile makeDirectory is " + makeDirectory);
            //切换文件路径
            boolean changeWorking = ftpClient.changeWorkingDirectory(remotePath);
            Log.i(TAG, "uploadFile changeWorking is " + changeWorking);
            File file = new File(localPath);
            if (null == file || !file.exists()) {
                throw new Exception("找不到本地文件，请检查相关配置路径!");
            }
            inputStream = new FileInputStream(file);
            if (remotePath.isEmpty()) {
                throw new Exception("Ftp上传路径不能为空，请检查相关配置路径！");
            }
            if (remoteFileName.isEmpty()) {
                throw new Exception("Ftp上传名称不能为空，请检查相关配置路径！");
            }
            String remote = remotePath + remoteFileName;
            Log.i(TAG, "uploadFile  remote is " + remote);
            //boolean isUpload = ftpClient.storeFile(remote, inputStream);
            boolean isUpload = ftpClient.storeFile(remoteFileName, inputStream);
            if (isUpload) {
                Log.i(TAG, "uploadFile  文件上传成功");
            } else {
                Log.i(TAG, "uploadFile  文件上传失败");
            }
        } catch (Exception e) {
            Log.i(TAG, "uploadFile Exception e" + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                Log.i(TAG, "uploadFile finally ftpClient disconnect");
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}