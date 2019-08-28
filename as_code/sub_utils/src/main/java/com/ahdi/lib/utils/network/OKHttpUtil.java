package com.ahdi.lib.utils.network;

import com.ahdi.lib.utils.config.Constants;
import com.ahdi.lib.utils.utils.AppGlobalUtil;
import com.ahdi.lib.utils.utils.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class OKHttpUtil {

    private static final String TAG = OKHttpUtil.class.getSimpleName();
    private static final int bufSize = 1024 * 8;
    private static final String PREFIX = "--", LINE_END = "\r\n";

    /**
     * POST请求
     *
     * @param urlString
     * @param paramBytes
     * @return
     */
    public static byte[] sendPostRequestCallbackByte(String urlString, byte[] paramBytes) throws Exception {
        byte[] result = null;
        HttpURLConnection httpURLConnection;
//        if (ConfigHelper.DEBUG) {
//            urlString = urlString + "?_debug";
//        }
        LogUtil.i(TAG, "http request link: " + urlString);

        trustAllHosts();//解决~~https请求时，会报出SSL握手异常

        URL url = new URL(urlString);
        httpURLConnection = (HttpURLConnection) url.openConnection();

        httpURLConnection.setRequestMethod(Constants.REQUEST_METHOD_POST);
        httpURLConnection.setUseCaches(false);//不使用缓存
        httpURLConnection.setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT);//连接服务器超时（单位：毫秒）
        httpURLConnection.setReadTimeout(Constants.HTTP_READ_TIMEOUT);//从服务器读取数据超时（单位：毫秒）
        httpURLConnection.setDoInput(true);// 设置运行输入
        httpURLConnection.setDoOutput(true);// 设置运行输出
        //设定传送的内容类型是可序列化的java对象
        // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
        httpURLConnection.addRequestProperty("Content-type", "application/json");
        httpURLConnection.addRequestProperty("charset", "UTF-8");
        httpURLConnection.addRequestProperty("Accept-Encoding", "gzip, deflate");//HttpURLConnection 默认使用gzip压缩和自动解压缩
//            httpURLConnection.setRequestProperty("Accept-Encoding", "identity");//屏蔽gzip压缩

        //httpURLConnection.setRequestProperty("User-Agent", AppGlobalUtil.getInstance().getUserAgent());

        // 将请求的数据写入输出流中
        OutputStream os = httpURLConnection.getOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(os);
        bos.write(paramBytes);
        bos.flush();// 刷新对象输出流，将任何字节都写入潜在的流中
        bos.close();
        os.close();

        int responseCode = httpURLConnection.getResponseCode();
        LogUtil.i(TAG, "httpURLConnection.getResponseCode() POST:" + responseCode);
        if (HttpURLConnection.HTTP_OK == responseCode) {
            InputStream inputStream = httpURLConnection.getInputStream();
            if ("gzip".equals(httpURLConnection.getHeaderField("Content-Encoding"))) {
                inputStream = new GZIPInputStream(new BufferedInputStream(inputStream));
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int len = -1;
            byte[] buffer = new byte[bufSize];
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            result = outputStream.toByteArray();
            outputStream.close();
            inputStream.close();

            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

        } else {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            throw new HttpStateException(String.valueOf(responseCode));
        }

        return result;
    }

    /**
     * GET 请求
     *
     * @param requestUrl
     * @return
     * @throws Exception
     */
    public static String sendGetRequestCallbackString(String requestUrl) throws Exception {

        trustAllHosts();
        LogUtil.i(TAG, "httpURLConnection  GET URL :" + requestUrl);

        //建立连接
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(Constants.REQUEST_METHOD_GET);
        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.setUseCaches(false);

        connection.setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT);
        connection.setReadTimeout(Constants.HTTP_READ_TIMEOUT);
        connection.setRequestProperty("User-Agent", AppGlobalUtil.getInstance().getUserAgent());
        connection.connect();

        //获取响应状态
        int responseCode = connection.getResponseCode();
        LogUtil.i(TAG, "httpURLConnection.getResponseCode() GET:" + responseCode);

        if (HttpURLConnection.HTTP_OK == responseCode) { //连接成功
            //当正确响应时处理数据
            StringBuffer buffer = new StringBuffer();
            String readLine;
            BufferedReader responseReader;
            //处理响应流
            responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((readLine = responseReader.readLine()) != null) {
                buffer.append(readLine).append("\n");
            }
            responseReader.close();
            LogUtil.d(TAG, "GET Result: " + buffer.toString());
            //JSONObject result = new JSONObject(buffer.toString());
            if (connection != null) {
                connection.disconnect();
            }

            return buffer.toString();
        } else {
            if (connection != null) {
                connection.disconnect();
            }
            throw new HttpStateException(String.valueOf(responseCode));
        }
    }


    /**
     * 上传图片
     *
     * @param upUrl
     * @param imgByte
     * @return
     */
    public static String uploadPhoto(String upUrl, byte[] imgByte) throws Exception {
        trustAllHosts();
        String result = null;
        String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
        URL url = new URL(upUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(Constants.HTTP_CONNECT_TIMEOUT);
        conn.setReadTimeout(Constants.HTTP_READ_TIMEOUT);
        conn.setDoInput(true); // 允许输入流
        conn.setDoOutput(true); // 允许输出流
        conn.setUseCaches(false); // 不允许使用缓存
        conn.setRequestMethod("POST"); // 请求方式
        conn.setRequestProperty("Charset", "UTF-8"); // 设置编码
        conn.setRequestProperty("connection", "keep-alive");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);
        conn.setRequestProperty("User-Agent", AppGlobalUtil.getInstance().getUserAgent());
        if (imgByte != null) {
            /**
             * 当文件不为空，把文件包装并且上传
             */
            OutputStream outputSteam = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outputSteam);
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */

            sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"pic.jpg\"" + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset=UTF-8" + LINE_END);
            sb.append(LINE_END);
            dos.write(sb.toString().getBytes());
            dos.write(imgByte, 0, imgByte.length);
            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功 当响应成功，获取响应的流
             */
            int responseCode = conn.getResponseCode();
            LogUtil.i(TAG, "httpURLConnection.getResponseCode() POST:" + responseCode);
            if (HttpURLConnection.HTTP_OK == responseCode) {
                InputStream inputStream = conn.getInputStream();
                if ("gzip".equals(conn.getHeaderField("Content-Encoding"))) {
                    inputStream = new GZIPInputStream(new BufferedInputStream(inputStream));
                }
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                int len2 = -1;
                byte[] buffer = new byte[bufSize];
                while ((len2 = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len2);
                }
                result = outputStream.toString();
                outputStream.close();
                inputStream.close();
                if (conn != null) {
                    conn.disconnect();
                }

            } else {
                if (conn != null) {
                    conn.disconnect();
                }
                if (HttpURLConnection.HTTP_UNAUTHORIZED == responseCode) {
                    LogUtil.d(TAG, "responseCode = 401 上传图片地址后面附带的token无效");
                }
                throw new HttpStateException(String.valueOf(responseCode));
            }

        }
        return result;
    }


    private static void trustAllHosts() {
        /** 学习地址 http://blog.csdn.net/james_liao3/article/details/52587019 */
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static class HttpStateException extends Exception {

        HttpStateException(String exception) {
            super(exception);
        }
    }
}
