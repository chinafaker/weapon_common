package com.abupdate.packer;

import org.gradle.internal.impldep.org.eclipse.jgit.annotations.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtils {

    public static void delFileInDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] tmp = dir.listFiles();
            if (tmp == null) {
                return;
            }
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isDirectory() && tmp[i].listFiles() != null && tmp[i].listFiles().length > 0) {
                    deleteDir(path + "/" + tmp[i].getName());
                } else {
                    tmp[i].delete();
                }
            }
        }
    }

    public static void deleteDir(@NonNull String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            return;
        }
        if (dir.isFile()) {
            dir.delete();
        } else if (dir.isDirectory()) {
            File[] tmp = dir.listFiles();
            if (tmp == null) {
                dir.delete();
                return;
            }
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isDirectory()) {
                    deleteDir(tmp[i].getAbsolutePath());
                } else {
                    tmp[i].delete();
                }
            }
            dir.delete();
        }
    }

    /**
     * 读取文件 返回string   换行
     *
     * @param filePath
     * @return
     */
    public static String readFile2String(String filePath) {
        String result = "";
        FileInputStream inputStream = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                return result;
            }
            inputStream = new FileInputStream(file);
            StringBuilder sb = new StringBuilder(inputStream.available());
            byte[] buffer = new byte[1024 * 8];
            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, len));
            }
            result = sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 创建文件并写入内容
     *
     * @param str
     * @param filePath
     */
    public static void createFile(String str, String filePath) {
        FileOutputStream outStream = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (null != outStream) {
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
