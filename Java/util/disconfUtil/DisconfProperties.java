package uyun.show.server.domain.util.disconfUtil;

import uyun.show.server.domain.Constants;
import uyun.show.server.domain.util.HttpUtil;
import uyun.show.server.domain.util.IpUtil;
import uyun.whale.common.encryption.http.DecryptRequest;
import uyun.whale.common.encryption.http.HttpEncryptionWrapper;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取disocnf配置相关信息
 *
 * @author weixy
 * @create 2018年8月13日
 */
public class DisconfProperties {

    /**
     * @param fileName,key
     * @return String
     * @desc 获取相关值
     */
    public static String GetValue(String fileName, String key) {

        if (fileName.isEmpty()) {
            return "";
        }

        String uri = "";
        String result = "";
        try {
            uri = Constants.UyunUrl + "disconf/api/config/file?app=uyun&env=local&version=2_0_0&key=" + fileName;
            result = HttpUtil.get(uri);
            if (result.isEmpty() || key.isEmpty()) {
                return result;
            }

            String[] results = result.split("\n");
            if (results == null || results.length == 0) {
                return "";
            }
            for (int i = 0; i < results.length; i++) {
                if (results[i].isEmpty()) {
                    continue;
                }
                String[] keys = results[i].split("=");
                if (keys == null || keys.length < 2) {
                    continue;
                }
                if (keys[0].trim().equals(key)) {
                    if (keys[1].startsWith(">>>") && keys[1].endsWith("<<<")) {
                        try {
                            DecryptRequest request = new DecryptRequest();
                            request.setUrl(String.format("http://%s:7550/daemon/api/v2/encryption/decrypt", IpUtil.getLocalIp()));
                            request.setEncryptedText(keys[1]);
                            keys[1] = HttpEncryptionWrapper.decrypt(request);
                        } catch (Exception e) {
                            return keys[1];
                        }
                    }
                    return keys[1].trim();
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
        return "";
    }

    /**
     * @param fileName
     * @return Map<String, String>
     * @desc 获取相关值
     */
    public static Map<String, String> GetMapValue(String fileName) {
        Map<String, String> mapValue = new HashMap<>();
        if (fileName.isEmpty()) {
            return mapValue;
        }

        String uri = "";
        String result = "";
        try {
            uri = Constants.UyunUrl+ "disconf/api/config/file?app=uyun&env=local&version=2_0_0&key=" + fileName;
            result = HttpUtil.get(uri);
            if (result.isEmpty()) {
                return mapValue;
            }

            String[] results = result.split("\n");
            if (results == null || results.length == 0) {
                return mapValue;
            }
            for (int i = 0; i < results.length; i++) {
                if (results[i].isEmpty()) {
                    continue;
                }
                String[] keys = results[i].split("=");
                if (keys == null || keys.length < 2) {
                    continue;
                }
                mapValue.put(keys[0].trim(), keys[1].trim());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return mapValue;
        }
        return mapValue;
    }
}
