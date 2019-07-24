package uyun.show.server.domain.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Base64Utils {

    protected final static Logger logger = LoggerFactory.getLogger(Base64Utils.class.getName());

    /**
     * @param imgFile 图片本地路径
     * @return
     * @desc 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     */
    public static String ImageToBase64ByLocal(String imgFile) {


        InputStream in = null;
        byte[] data = null;

        try {
            in = new FileInputStream(imgFile);

            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            logger.error("ImageToBase64ByLocal IOException : " + e);
        }

        BASE64Encoder encoder = new BASE64Encoder();

        return encoder.encode(data);
    }

    /**
     * @param bytes
     * @return
     * @desc bytes转换成base64字符串
     */
    public static String ImageToBase64ByBytes(byte[] bytes) {

        if (bytes == null) {
            return "";
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bytes);
    }

    /**
     * @param imgStr
     * @param imgFilePath
     * @return
     * @desc 对字节数组字符串进行Base64解码并生成图片
     */
    public static boolean Base64ToImage(String imgStr, String imgFilePath) {

        if (imgStr.isEmpty()) // 图像数据为空
            return false;

        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] b = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }

            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(b);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            logger.error("Base64ToImage IOException : " + e);
            return false;
        }
    }

    /**
     * @param imgURL 图片线上路径
     * @return
     * @desc 在线图片转换成base64字符串
     */
    public static String ImageToBase64ByOnline(String imgURL) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new Base64Utils().new NullHostNameVerifier());
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // 创建URL
            URL url = new URL(imgURL);
            byte[] by = new byte[1024];
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            // 将内容读取内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            // 关闭流
            is.close();
        } catch (IOException e) {
            logger.error("ImageToBase64ByOnline IOException : " + e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("ImageToBase64ByOnline IOException : " + e);
        } catch (KeyManagementException e) {
            logger.error("ImageToBase64ByOnline IOException : " + e);
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data.toByteArray());
    }

    static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    }};

    public class NullHostNameVerifier implements HostnameVerifier {
        /*
         * (non-Javadoc)
         *
         * @see javax.net.ssl.HostnameVerifier#verify(java.lang.String,
         * javax.net.ssl.SSLSession)
         */
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            // TODO Auto-generated method stub
            return true;
        }
    }
}
