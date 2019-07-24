package uyun.show.server.domain.util;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uyun.show.server.domain.Constants;

import javax.net.ssl.*;

public class FileUtil {

    protected static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 解压tar File
     *
     * @param file      要解压的tar文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @throws IOException
     */
    public static void unTar(File file, String outputDir) throws IOException {
        TarInputStream tarIn = null;
        try {
            tarIn = new TarInputStream(new FileInputStream(file), 1024 * 2);
            createDirectory(outputDir, null);// 创建输出目录
            TarEntry entry = null;
            while ((entry = tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {// 是目录
                    createDirectory(outputDir, entry.getName());// 创建空目录
                } else {// 是文件
                    File tmpFile = new File(outputDir + "/" + entry.getName());
                    createDirectory(tmpFile.getParent() + "/", null);// 创建输出目录
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(tmpFile);
                        int length = 0;
                        byte[] b = new byte[2048];
                        while ((length = tarIn.read(b)) != -1) {
                            out.write(b, 0, length);
                        }
                    } catch (IOException ex) {
                        throw ex;
                    } finally {
                        if (out != null)
                            out.close();
                    }
                }
            }
        } catch (IOException ex) {
            throw new IOException("解压归档文件出现异常", ex);
        } finally {
            try {
                if (tarIn != null) {
                    tarIn.close();
                }
            } catch (IOException ex) {
                throw new IOException("关闭tarFile出现异常", ex);
            }
        }
    }

    public static void unTarGz(File file, String outputDir) throws IOException {
        TarInputStream tarIn = null;
        try {
            tarIn = new TarInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(file))), 1024 * 2);
            createDirectory(outputDir, null);// 创建输出目录

            TarEntry entry = null;
            while ((entry = tarIn.getNextEntry()) != null) {

                if (entry.isDirectory()) {// 是目录
                    entry.getName();
                    createDirectory(outputDir, entry.getName());// 创建空目录
                } else {// 是文件
                    File tmpFile = new File(outputDir + "/" + entry.getName());
                    createDirectory(tmpFile.getParent() + "/", null);// 创建输出目录
                    OutputStream out = null;
                    try {
                        out = new FileOutputStream(tmpFile);
                        int length = 0;

                        byte[] b = new byte[2048];

                        while ((length = tarIn.read(b)) != -1) {
                            out.write(b, 0, length);
                        }

                    } catch (IOException ex) {
                        throw ex;
                    } finally {

                        if (out != null)
                            out.close();
                    }
                }
            }
        } catch (IOException ex) {
            throw new IOException("解压归档文件出现异常", ex);
        } finally {
            try {
                if (tarIn != null) {
                    tarIn.close();
                }
            } catch (IOException ex) {
                throw new IOException("关闭tarFile出现异常", ex);
            }
        }
    }

    /**
     * 构建目录
     *
     * @param outputDir
     * @param subDir
     */

    public static void createDirectory(String outputDir, String subDir) {
        File file = new File(outputDir);
        if (!(subDir == null || subDir.trim().equals(""))) {// 子目录不为空
            file = new File(outputDir + "/" + subDir);
        }

        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static boolean checkFileIsExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    public static void removeFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

    public static void copyFile(String srcPath, String destPath) throws IOException {
        File srcfile = new File(srcPath);
        if (!srcfile.exists()) {
            return;
        }
        File destfile = new File(destPath);
        if (!destfile.getParentFile().exists()) {
            destfile.getParentFile().mkdirs();
        }

        InputStream input = null;
        OutputStream output = null;
        try {
            input = new FileInputStream(srcfile);
            output = new FileOutputStream(destfile);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buf)) > 0) {
                output.write(buf, 0, bytesRead);
            }
        } finally {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * 复制一个目录及其子目录、文件到另外一个目录
     *
     * @param src
     * @param dest
     */
    public static void copyFolder(File src, File dest) {
        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }
            String files[] = src.list();
            for (String file : files) {
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // 递归复制
                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(src);
                out = new FileOutputStream(dest);
                byte[] buffer = new byte[1024];

                int length;

                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
            } catch (FileNotFoundException e) {
                logger.error("", e);
            } catch (IOException e) {
                logger.error("", e);
            }
        }
    }

    public static String readData(String filename) {
        String bgPath = Constants.HOMEPATH + "/data/window/v1/";
        if (!checkFileIsExist(bgPath)) {
            return null;
        }
        File file = new File(bgPath + "data.json");
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
        } catch (FileNotFoundException e) {
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return buffer.toString();
    }

    /**
     * 将文本文件中的内容读入到buffer中
     *
     * @param buffer   buffer
     * @param filePath 文件路径
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    /**
     * 读取文本文件内容
     *
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static String readFile(String filePath) {
        StringBuffer sb = new StringBuffer();
        try {
            readToBuffer(sb, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 解压缩zipFile
     *
     * @param file      要解压的zip文件对象
     * @param outputDir 要解压到某个指定的目录下
     * @throws IOException
     */
    public static void unZip(File file, String outputDir) throws IOException {
        ZipFile zipFile = null;

        try {
            Charset CP866 = Charset.forName("CP866"); // specifying alternative (non
            // UTF-8) charset
            // ZipFile zipFile = new ZipFile(zipArchive, CP866);
            zipFile = new ZipFile(file, CP866);
            createDirectory(outputDir, null);// 创建输出目录

            Enumeration<?> enums = zipFile.entries();
            while (enums.hasMoreElements()) {

                ZipEntry entry = (ZipEntry) enums.nextElement();
                System.out.println("解压." + entry.getName());

                if (entry.isDirectory()) {// 是目录
                    createDirectory(outputDir, entry.getName());// 创建空目录
                } else {// 是文件
                    File tmpFile = new File(outputDir + "/" + entry.getName());
                    createDirectory(tmpFile.getParent() + "/", null);// 创建输出目录

                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = zipFile.getInputStream(entry);
                        ;
                        out = new FileOutputStream(tmpFile);
                        int length = 0;

                        byte[] b = new byte[2048];
                        while ((length = in.read(b)) != -1) {
                            out.write(b, 0, length);
                        }

                    } catch (IOException ex) {
                        throw ex;
                    } finally {
                        if (in != null)
                            in.close();
                        if (out != null)
                            out.close();
                    }
                }
            }

        } catch (IOException e) {
            throw new IOException("解压缩文件出现异常", e);
        } finally {
            try {
                if (zipFile != null) {
                    zipFile.close();
                }
            } catch (IOException ex) {
                throw new IOException("关闭zipFile出现异常", ex);
            }
        }
    }

    public static void writeFile(File file, String data) throws IOException {
        Writer out = null;
        try {
            if (!file.getParentFile().exists()) { // 判断文件父目录是否存在
                file.getParentFile().mkdirs();
            }

            if (!file.exists()) {
                file.createNewFile();
            }
            out = new FileWriter(file);
            out.write(data);
            out.close();
        } catch (IOException e) {
            throw new IOException("关闭zipFile出现异常", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public static boolean tarGz(String srcPath, String fileName) {
        String tarName = fileName.replace(".gz", "");
        File tarFile = getTar(srcPath, tarName);// 生成的tar文件
        File gzFile = new File(fileName);// 将要生成的压缩文件

        GZIPOutputStream out = null;
        InputStream in = null;
        boolean boo = false;// 是否成功
        try {
            in = new FileInputStream(tarFile);
            out = new GZIPOutputStream(new FileOutputStream(gzFile), 1024 * 2);
            byte[] b = new byte[1024 * 2];
            int length = 0;
            while ((length = in.read(b)) != -1) {
                out.write(b, 0, length);
            }

            boo = true;
        } catch (Exception ex) {
            logger.error("压缩归档文件失败", ex);
        } finally {

            try {
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            } catch (IOException ex) {
                logger.error("关闭流出现异常", ex);
            } finally {
                if (!boo) {// 清理操作
                    tarFile.delete();

                    if (gzFile.exists())
                        gzFile.delete();
                }
            }
        }
        return boo;
    }

    public static File getTar(String srcPath, String tarName) {
        File srcFile = new File(srcPath);// 要归档的文件对象
        File targetTarFile = new File(tarName);// 归档后的文件名
        TarOutputStream out = null;
        boolean boo = false;// 是否压缩成功
        try {
            out = new TarOutputStream(new BufferedOutputStream(new FileOutputStream(targetTarFile)));
            tar(srcFile, out, "", true);
            boo = true;
            // 归档成功
            return targetTarFile;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {

            try {
                if (out != null)
                    out.close();
            } catch (IOException ex) {
                throw new RuntimeException("关闭Tar输出流出现异常", ex);
            } finally {
                // 清理操作
                if (!boo && targetTarFile.exists())// 归档不成功,
                    targetTarFile.delete();
            }
        }
    }

    /**
     * 归档tar文件
     *
     * @param file 归档的文件对象
     * @param out  输出tar流
     * @param dir  相对父目录名称
     * @param boo  是否把空目录归档进去
     */
    public static void tar(File file, TarOutputStream out, String dir, boolean boo) throws IOException {
        if (file.isDirectory()) {// 是目录
            File[] listFile = file.listFiles();// 得出目录下所有的文件对象
            if (listFile.length == 0 && boo) {// 空目录归档
                out.putNextEntry(new TarEntry(dir + file.getName() + "/"));// 将实体放入输出Tar流中
                System.out.println("归档." + dir + file.getName() + "/");
                return;
            } else {
                for (File cfile : listFile) {
                    tar(cfile, out, dir + file.getName() + "/", boo);// 递归归档
                }
            }
        } else if (file.isFile()) {// 是文件
            System.out.println("归档." + dir + file.getName() + "/");
            byte[] bt = new byte[2048 * 2];
            TarEntry ze = new TarEntry(dir + file.getName());// 构建tar实体
            // 设置压缩前的文件大小
            ze.setSize(file.length());
            // ze.setName(file.getName());//设置实体名称.使用默认名称
            out.putNextEntry(ze);//// 将实体放入输出Tar流中
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                int i = 0;
                while ((i = fis.read(bt)) != -1) {// 循环读出并写入输出Tar流中
                    out.write(bt, 0, i);
                }
            } catch (IOException ex) {
                throw new IOException("写入归档文件出现异常", ex);
            } finally {
                try {
                    if (fis != null)
                        fis.close();// 关闭输入流
                    out.closeEntry();
                } catch (IOException ex) {
                    throw new IOException("关闭输入流出现异常");
                }
            }
        }
    }

    public static List<File> GetFiles(String path) {

        List<File> files = new ArrayList<>();
        if (path == null || path.isEmpty()) {
            return files;
        }

        File file = new File(path);
        File[] fs = file.listFiles();    //遍历path下的文件和目录，放在File数组中
        for (File f : fs) {                    //遍历File[]数组
            if (!f.isDirectory())        //若非目录(即文件)，则打印
                files.add(f);
        }
        return files;
    }

    /**
     * @param imgURL 图片线上路径
     * @return
     * @desc 在线图片下载图片
     */
    public static ByteArrayOutputStream DownloadImageOnline(String imgURL) {
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
            logger.error("DownloadImageOnline IOException : " + e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("DownloadImageOnline IOException : " + e);
        } catch (KeyManagementException e) {
            logger.error("DownloadImageOnline IOException : " + e);
        }

        return data;
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
