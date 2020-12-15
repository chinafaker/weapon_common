package com.hhb.ep21client;

import android.os.Build;
import android.os.StatFs;
import android.text.TextUtils;


import com.abupdate.common.Trace;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 文件工具类，用于验证文件的 md5<p/>
 */
public class FileUtil {

    public static boolean rename(String src, String dest) {
        File file1 = new File(src);
        File file2 = new File(dest);
        return file1.renameTo(file2);
    }

    public static boolean copyFile(String oldPath, String newPath) {
        File newFile = new File(newPath);
        if (newFile.exists()) {
            newFile.delete();
        } else {
            if (!newFile.getParentFile().exists()) {
                createOrExistsDir(newFile.getParentFile().getAbsolutePath());
            }
        }
        try {
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            return true;
        } catch (Exception e) {
            Trace.e("FileUtil", "copyFile() e = " + e);
            e.printStackTrace();
            return false;
        }
    }

    public static String getMd5ByFile(File file) {
        FileInputStream fis = null;
        StringBuffer buf = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buffer = new byte[1024 * 256];
            int length = -1;
            // Trace.d(logTag, "getFileMD5, GenMd5 subAll");
            long s = System.currentTimeMillis();
            if (fis == null || md == null) {
                return null;
            }
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
            byte[] bytes = md.digest();
            if (bytes == null) {
                return null;
            }
            for (int i = 0; i < bytes.length; i++) {
                String md5s = Integer.toHexString(bytes[i] & 0xff);
                if (md5s == null || buf == null) {
                    return null;
                }
                if (md5s.length() == 1) {
                    buf.append("0");
                }
                buf.append(md5s);
            }
            // Trace.d(logTag, "getFileMD5, GenMd5 success! spend the time: "+ (System.currentTimeMillis() - s) + "ms");
            String value = buf.toString();
            int fix_num = 32 - value.length();
            for (int i = 0; i < fix_num; i++) {
                value = "0" + value;
            }
            return value;
        } catch (Exception ex) {
            Trace.d("FileUtil", "getFileMD5, Exception " + ex.toString());
            ex.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static String getMd5ByFile(String filePath) {
        File fd = new File(filePath);
        if (fd.exists()) {
            return getMd5ByFile(fd);
        }
        return "";
    }

    /**
     * @param filePath
     * @param md5sum   not null
     * @return 验证是否通过
     */
    public static boolean validateFile(String filePath, String md5sum) {
        if (TextUtils.isEmpty(filePath) || TextUtils.isEmpty(md5sum)) {
            Trace.w("FileUtil", "validateFile() filePath=%s,md5=%s", filePath, md5sum);
            return false;
        }
        String md5_file = getSha256ByFile(filePath);
        boolean success = md5sum.equalsIgnoreCase(md5_file);
        if (!success) {
            Trace.i("FileUtil", "validateFile() success=" + md5sum.equalsIgnoreCase(md5_file) + ",md5_file = " + md5_file + ",md5_net = " + md5sum);
        }
        return success;
    }

    /**
     * 计算返回文件hash值
     *
     * @param filePath
     * @return
     */
    public static String getSha256ByFile(String filePath) {
        FileInputStream var2 = null;
        StringBuilder var3 = new StringBuilder();

        try {
            MessageDigest var4 = MessageDigest.getInstance("SHA-256");
            var2 = new FileInputStream(filePath);
            byte[] var25 = new byte[262144];
            if (var4 != null) {
                int var26;
                while ((var26 = var2.read(var25)) != -1) {
                    var4.update(var25, 0, var26);
                }

                byte[] var27 = var4.digest();
                String var28;
                if (var27 == null) {
                    return null;
                } else {
                    for (int var8 = 0; var8 < var27.length; ++var8) {
                        String var9 = Integer.toHexString(var27[var8] & 255);
                        if (var9 == null) {
                            return null;
                        }

                        if (var9.length() == 1) {
                            var3.append("0");
                        }

                        var3.append(var9);
                    }

                    var28 = var3.toString();
                    return var28;
                }
            } else {
                return null;
            }
        } catch (Exception var23) {
            var23.printStackTrace();
            return null;
        } finally {
            try {
                if (var2 != null) {
                    var2.close();
                }
            } catch (IOException var22) {
                var22.printStackTrace();
            }

        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 目录路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(String dirPath) {
        if (isSpace(dirPath)) {
            return false;
        } else {
            File path = new File(dirPath);
            return path != null && (path.exists() ? path.isDirectory() : path.mkdirs());
        }
    }

    /**
     * 递归删除文件夹 要利用File类的delete()方法删除目录时， 必须保证该目录下没有文件或者子目录，否则删除失败，
     * 因此在实际应用中，我们要删除目录， 必须利用递归删除该目录下的所有子目录和文件， 然后再删除该目录。
     *
     * @param path
     */
    public static void delFileInDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] tmp = dir.listFiles();
            if (tmp == null) {
                return;
            }
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isDirectory()) {
                    delFileInDir(path + "/" + tmp[i].getName());
                } else {
                    tmp[i].delete();
                }
            }
        }
    }

    /**
     * 删除文件夹（包括文件夹内的文件、文件夹）
     *
     * @param path
     */
    public static void delDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            File[] tmp = dir.listFiles();
            if (tmp == null) {
                return;
            }
            for (int i = 0; i < tmp.length; i++) {
                if (tmp[i].isDirectory()) {
                    delFileInDir(path + "/" + tmp[i].getName());
                } else {
                    tmp[i].delete();
                }
            }
            dir.delete();
        }
    }

    /**
     * 删除单个文件
     *
     * @param pathName 要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFileByPath(String pathName) {
        try {
            File file = new File(pathName);
            if (file.isFile() && file.exists()) {
                return file.delete();
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Trace.i("FileUtil", "deleteFileByPath" + e.toString());
        }
        return false;
    }

    public static String convertFileSize(long size) {
        float result = size;
        String suffix = "B";
        if (result > 1024) {
            suffix = "KB";
            result = result / 1024;
        }
        if (result > 1024) {
            suffix = "MB";
            result = result / 1024;
        }
        if (result > 1024) {
            suffix = "GB";
            result = result / 1024;
        }
        if (result > 1024) {
            suffix = "TB";
            result = result / 1024;
        }

        final String roundFormat;
        if (result < 10) {
            roundFormat = "%.2f";
        } else if (result < 100) {
            roundFormat = "%.1f";
        } else {
            roundFormat = "%.0f";
        }
        return String.format(roundFormat + suffix, result);
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

    /**
     * 用于判断指定字符是否为空白字符，空白符包含：空格、tab键、换行符。
     *
     * @param s
     * @return
     */
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

    public static boolean fileRename(String srcFile, String desFile) {
        File src = new File(srcFile);
        File des = new File(desFile);
        return src.renameTo(des);
    }

    /**
     * 批量压缩文件
     *
     * @param resFiles    待压缩文件路径集合
     * @param zipFilePath 压缩文件路径
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFiles(final Collection<String> resFiles,
                                   final String zipFilePath)
            throws IOException {
        return zipFiles(resFiles, zipFilePath, null);
    }

    /**
     * 批量压缩文件
     *
     * @param resFilePaths 待压缩文件路径集合
     * @param zipFilePath  压缩文件路径
     * @param comment      压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO错误时抛出
     */
    public static boolean zipFiles(final Collection<String> resFilePaths,
                                   final String zipFilePath,
                                   final String comment)
            throws IOException {
        if (resFilePaths == null || zipFilePath == null)
            return false;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
            for (String resFile : resFilePaths) {
                if (!zipFile(getFileByPath(resFile), "", zos, comment))
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

    /**
     * 压缩文件
     *
     * @param resFilePath 待压缩文件路径
     * @param zipFilePath 压缩文件路径
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO 错误时抛出
     */
    public static boolean zipFile(File resFilePath, File zipFilePath)
            throws IOException {
        return zipFile(resFilePath, zipFilePath, null);
    }

    public static boolean zipFile(InputStream resFileStream, File zipFilePath, String rootPath) throws IOException {
        InputStream is = null;
        ZipOutputStream zos = null;
        try {
            is = new BufferedInputStream(resFileStream);
            zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
            ZipEntry entry = new ZipEntry(rootPath);
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

        if (zipFilePath.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 压缩文件
     *
     * @param resFile 待压缩文件
     * @param zipFile 压缩文件
     * @param comment 压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO 错误时抛出
     */
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

    /**
     * 压缩文件
     *
     * @param resFile  待压缩文件
     * @param rootPath 相对于压缩文件的路径
     * @param zos      压缩文件输出流
     * @param comment  压缩文件的注释
     * @return {@code true}: 压缩成功<br>{@code false}: 压缩失败
     * @throws IOException IO 错误时抛出
     */
    private static boolean zipFile(final File resFile,
                                   String rootPath,
                                   final ZipOutputStream zos,
                                   final String comment)
            throws IOException {
        rootPath = rootPath + (isSpace(rootPath) ? "" : File.separator) + resFile.getName();
        if (resFile.isDirectory()) {
            File[] fileList = resFile.listFiles();
            // 如果是空文件夹那么创建它，我把'/'换为File.separator测试就不成功，eggPain
            if (fileList == null || fileList.length <= 0) {
                ZipEntry entry = new ZipEntry(rootPath + '/');
                entry.setComment(comment);
                zos.putNextEntry(entry);
                zos.closeEntry();
            } else {
                for (File file : fileList) {
                    // 如果递归返回 false 则返回 false
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

    /**
     * 获取文件夹下面的所有文件名字（不包含文件夹）
     *
     * @param path
     * @return
     */
    public static ArrayList<String> getFileNames(String path) {
        ArrayList<String> fileNames = new ArrayList<>();
        try {
            File file = new File(path);
            if (file.isDirectory() && file.exists()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isFile() && files[i].exists()) {
                        String nameStr = files[i].getName();
                        fileNames.add(nameStr);
                    }
                }
            }
        } catch (Exception ex) {
            Trace.d("FileUtil", "getFileNames()" + ex.toString());
            ex.printStackTrace();
        }
        return fileNames;
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
            Trace.d("FileUtil", "stringToFile()" + ex.toString());
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

    /**
     * Usb升级、创建文件并写入内容，删除之前的ota文件
     * 返回成功、失败
     *
     * @param fileContent
     * @param filePath
     */
    public static boolean writeFile(String fileContent, String filePath) {
        FileOutputStream outStream = null;
        try {
            File file = new File(filePath);
            //删除之前的 .ota文件
            String parentPath = file.getParent();
            File dirs = new File(parentPath);
            if (dirs.exists()) {
                File[] tmp = dirs.listFiles();
                if (tmp != null && tmp.length > 0) {
                    for (int i = 0; i < tmp.length; i++) {
                        if (!tmp[i].isDirectory() && tmp[i].getName().endsWith(".ota") && tmp[i].getName().contains("_check_")) {
                            tmp[i].delete();
                        }
                    }
                }
            }
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            outStream = new FileOutputStream(file);
            outStream.write(fileContent.getBytes());
            return true;
        } catch (Exception ex) {
            Trace.d("FileUtil", "stringToFile()" + ex.toString());
            ex.printStackTrace();
            return false;
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

    /**
     * 读取文件 返回string   换行
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
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
     * 文件夹是否存在
     *
     * @param dirPath 文件夹路径
     * @return
     */
    public static boolean isDirExists(String dirPath) {
        File file = new File(dirPath);
        Trace.d("FileUtil", "isDirExists() dirPath:  " + dirPath);
        if (file.exists() && file.isDirectory()) {
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(new File(file, "1.test"));
                fileOutputStream.write(1);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            deleteFileByPath(file + "/1.test");
            return true;
        }
        return false;
    }

    /**
     * 指定格式压缩日志文件
     *
     * @param traces
     * @return
     */
    public static File zipTraceLog(List<String> traces, String destLog) {
        if (traces == null || traces.size() == 0) {
            return null;
        }
        File destLogFile = new File(destLog);
        File parentFile = destLogFile.getParentFile();
        if (!destLogFile.getParentFile().exists()) {
            if (!createOrExistsDir(parentFile.getAbsolutePath())) {
                return null;
            }
        }
        try {
            if (zipFiles(traces, destLog)) {
                return new File(destLog);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得剩余磁盘空间大小;单位 B
     *
     * @param path
     * @return
     */
    public static long get_storage_free_size(String path) {
        File file = new File(path);
        if (!file.isDirectory()) {
            file = file.getParentFile();
        }
        StatFs statFs = null;
        try {
            statFs = new StatFs(file.getAbsolutePath());
        } catch (Exception e) {
            return -1;
        }
        long availableBlocksLong;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocksLong = statFs.getAvailableBlocksLong();
        } else {
            availableBlocksLong = statFs.getAvailableBlocks();
        }

        long blockSizeLong;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSizeLong = statFs.getBlockSizeLong();
        } else {
            blockSizeLong = statFs.getBlockSize();
        }
        return availableBlocksLong * blockSizeLong;
    }

    private static File getFileByPath(final String filePath) {
        return isSpace(filePath) ? null : new File(filePath);
    }

}
