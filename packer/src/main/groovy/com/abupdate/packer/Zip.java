package com.abupdate.packer;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {

    public static boolean zipFiles(final File[] resFiles,
                                   final String zipFilePath)
            throws IOException {
        List<File> files = Arrays.asList(resFiles);
        return zipFiles(files, zipFilePath);
    }

    public static boolean zipFiles(final Collection<File> resFiles,
                                   final String zipFilePath)
            throws IOException {
        return zipFiles(resFiles, zipFilePath, null);
    }

    public static boolean zipFiles(final Collection<File> resFilePaths,
                                   final String zipFilePath,
                                   final String comment)
            throws IOException {
        if (resFilePaths == null || zipFilePath == null)
            return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
            for (File resFile : resFilePaths) {
                if (!zipFile(getFileByPath(resFile.getAbsolutePath()), "", zos, comment))
                    return false;
            }
            return true;
        } finally {
            if (zos != null) {
                zos.finish();
                closeIO(zos);
            }
        }
    }

    public static boolean zipFile(File resFilePath, File zipFilePath)
            throws IOException {
        return zipFile(resFilePath, zipFilePath, null);
    }

    public static boolean zipFile(final File resFile,
                                  final File zipFile,
                                  final String comment)
            throws IOException {
        if (resFile == null || zipFile == null)
            return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            return zipFile(resFile, "", zos, comment);
        } finally {
            if (zos != null) {
                closeIO(zos);
            }
        }
    }

    private static boolean zipFile(final File resFile,
                                   String rootPath,
                                   final ZipOutputStream zos,
                                   final String comment)
            throws IOException {
        rootPath = rootPath + (isSpace(rootPath) ? "" : File.separator) + resFile.getName();
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            if (fileList == null || fileList.length <= 0) {
                ZipEntry entry = new ZipEntry(rootPath + '/');
                entry.setComment(comment);
                zos.putNextEntry(entry);
                zos.closeEntry();
            } else {
                for (File file : fileList) {
                    if (!zipFile(file, rootPath, zos, comment))
                        return false;
                }
            }
        } else {
            InputStream is = null;
            try {
                is = new BufferedInputStream(new FileInputStream(resFile));
                ZipEntry entry = new ZipEntry(rootPath);
                entry.setComment(comment);
                zos.putNextEntry(entry);
                byte buffer[] = new byte[1024];
                int len;

                while ((len = is.read(buffer)) != -1) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
            } finally {
                closeIO(is);
            }
        }
        return true;
    }

    public static void closeIO(Closeable... closeables) {
        if (closeables == null) {
            return;
        }
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isSpace(String s) {
        if (s == null) {
            return true;
        }
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

}
