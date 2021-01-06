package com.hhb.ep21client;

import android.util.Log;

import androidx.annotation.NonNull;

import com.abupdate.common.Trace;
import com.abupdate.common_download.DownConfig;
import com.abupdate.common_download.DownEntity;
import com.abupdate.common_download.DownError;
import com.abupdate.common_download.DownManager;
import com.abupdate.common_download.DownTask;
import com.abupdate.common_download.listener.SimpleDownListener;
import com.abupdate.common_download.verifymode.Impl.VerifySha256Impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadUtil {
    private static final String TAG = "DownloadUtil";
    private static DownloadUtil downloadUtil;
    private final OkHttpClient okHttpClient;
    private int lastProgress = 0;

    public static DownloadUtil get() {
        if (downloadUtil == null) {
            downloadUtil = new DownloadUtil();
        }
        return downloadUtil;
    }

    private DownloadUtil() {
        OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
//        mBuilder.sslSocketFactory(createSSLSocketFactory());
//        mBuilder.hostnameVerifier(new TrustAllHostnameVerifier());
        okHttpClient = mBuilder.build();
    }

    /**
     * @param url      下载连接
     * @param saveDir  储存下载文件的SDCard目录
     * @param listener 下载监听
     */
    public void download(final String url, final String saveDir, final OnDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败
                Log.e("download", "onFailure: ", e);
                listener.onDownloadFailed(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                // 储存下载文件的目录
                String savePath = isExistDir(saveDir);
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
//                    File file = new File(savePath, getNameFromUrl(url));
                    File file = new File(savePath, "9e511a80cecb4871b5da4fd2c0457361.xml");
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载中
                        if (progress > lastProgress) {
                            lastProgress = progress;
                            listener.onDownloading(progress);
                        }
                    }
                    fos.flush();
                    // 下载完成
                    listener.onDownloadSuccess();
                } catch (Exception e) {
                    Log.e("Download", "下载失败", e);
                    listener.onDownloadFailed(e);
                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    /**
     * @param saveDir
     * @return
     * @throws IOException 判断下载目录是否存在
     */
    private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(saveDir);
        if (!downloadFile.exists()) {
            downloadFile.mkdirs();
        }
        //        if (!downloadFile.mkdirs()) {
        //            downloadFile.createNewFile();
        //        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return 从下载连接中解析出文件名
     */
    @NonNull
    public static String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed(Exception e);
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
            Log.d("FileUtil", "getFileMD5, Exception " + ex.toString());
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

    public interface OnUploadListener {
        /**
         * 下载成功
         */
        void onSuccess();


        /**
         * 下载失败
         */
        void onFailed();
    }

    // 使用OkHttp上传文件
    public void uploadFile(File file, final OnUploadListener onUploadListener) {
        OkHttpClient client = new OkHttpClient();
        MediaType contentType = MediaType.parse("text/plain"); // 上传文件的Content-Type
        RequestBody body = RequestBody.create(contentType, file); // 上传文件的请求体
        Request request = new Request.Builder()
                .url("http://192.168.225.1:8080/") // 上传地址
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // 文件上传成功
                if (response.isSuccessful()) {
                } else {
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                // 文件上传失败
                Log.e("download", "onFailure: ", e);
            }
        });
    }


    public static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }
        return ssfFactory;
    }


    public static void executeDownload(String crtPath, String url, String localFilePath, long fileSize, String hash) {
        DownConfig.setVerifyMode(VerifySha256Impl.INSTANCE);
        Trace.i(TAG, "executeDownload() start ");
        File file = new File(crtPath);
        if (!file.exists()) {
            Trace.i(TAG, "executeDownload() file not exists");
            return;
        }
        DownConfig.setCRT(crtPath);
        DownEntity downEntity = new DownEntity()
                .setUrl(url)
                .setFilePath(localFilePath)
                .setFileSize(fileSize)
                .setVerifyCode(hash);
        DownTask downTask = new DownTask();
        downTask.add(downEntity);
        downTask.setListener(new SimpleDownListener() {
            @Override
            public void on_manual_cancel() {
                super.on_manual_cancel();
                Trace.i(TAG, "on_manual_cancel()");
            }

            @Override
            public void on_progress(DownEntity entity, int progress, long down_size, long total_size) {
                super.on_progress(entity, progress, down_size, total_size);
                Trace.i(TAG, "on_progress() progress is " + progress + " down_size is " + down_size + "total_size is " + total_size);
            }

            @Override
            public void on_success(DownEntity downEntity) {
                super.on_success(downEntity);
                Trace.i(TAG, "on_success()");
            }

            @Override
            public void on_failed(DownEntity errDownEntity) {
                super.on_failed(errDownEntity);
                Trace.i(TAG, "on_failed() downEntity.download_status is " + downEntity.download_status);
            }
        });
        //执行下载
        DownManager.execAsync(downTask);
    }
}