package com.hhb.appdemo;


import android.util.Log;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FtpUtilTest {
    private static final String TAG = "FtpUtilTest";

    public static void uploadFileTest(String host, String port, String username, String password, String remotePath, String remoteFileName, String localPath) {
        Log.i(TAG, "uploadFile start");
        if (host.isEmpty()) {
            Log.i(TAG, "Ftp上传主机ip不能为空，请重新配置");
            return;
        }
        try {
            if (!port.isEmpty()) {
                int pt = Integer.parseInt(port);
                Log.i(TAG, "uploadFile port is " + pt);
            }
        } catch (NumberFormatException e) {
            Log.i(TAG, "端口号非法，请检查相关配置!");
            return;
        }
        if (remotePath.isEmpty()) {
            Log.i(TAG, "Ftp上传路径不能为空，请检查相关配置路径！");
            return;
        }
        if (remoteFileName.isEmpty()) {
            Log.i(TAG, "Ftp上传名称不能为空，请检查相关配置路径！");
            return;
        }
        File file = new File(localPath);
        if (!file.exists()) {
            Log.i(TAG, "找不到本地文件，请检查相关配置路径!");
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
                Log.i(TAG, "uploadFile login success ");
            } else {
                Log.i(TAG, "uploadFile login failed ");
                return;
            }
            //创建文件夹
            boolean makeDirectory = ftpClient.makeDirectory(remotePath);
            Log.i(TAG, "uploadFile makeDirectory is " + makeDirectory);
            //切换文件路径
            boolean changeWorking = ftpClient.changeWorkingDirectory(remotePath);
            Log.i(TAG, "uploadFile changeWorking is " + changeWorking);

            inputStream = new FileInputStream(file);
            String remote = remotePath + remoteFileName;
            Log.i(TAG, "uploadFile  remote is " + remote);
            boolean isUpload = ftpClient.storeFile(remote, inputStream);
            if (isUpload) {
                Log.i(TAG, "uploadFile  upload file success");
            } else {
                Log.i(TAG, "uploadFile  upload file failed");
            }
        } catch (IOException e) {
            Log.i(TAG, "uploadFile IOException e" + e.getMessage());
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