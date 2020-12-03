package com.abupdate.packer;

import org.gradle.internal.impldep.org.eclipse.jgit.annotations.NonNull;

import java.io.File;

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

}
