package uyun.show.server.domain.util;

import com.baidu.disconf.client.usertools.DisconfDataGetter;
import org.apache.commons.lang3.StringUtils;
import uyun.whale.common.encryption.http.DecryptRequest;
import uyun.whale.common.encryption.http.HttpEncryptionWrapper;

import java.util.Objects;

public class ConfigUtils {

    public static String getValue(String key, String defaultValue) {
        String returnValue = null;
        if (StringUtils.isBlank(defaultValue)) {
            returnValue = DisconfDataGetter.getByFile("common.properties").get(key) == null ? null
                    : DisconfDataGetter.getByFile("common.properties").get(key).toString();
        } else {
            returnValue = defaultValue;
        }
        return decryptKey(key, returnValue);
    }

    public static String getFileValue(String filename, String key) {
        String returnValue = DisconfDataGetter.getByFile(filename).get(key) == null ? null
                : DisconfDataGetter.getByFile(filename).get(key).toString();
        return decryptKey(key, returnValue);
    }

    public static String getValue(String key) {
        String returnValue = DisconfDataGetter.getByFile("common.properties").get(key) == null ? null
                : DisconfDataGetter.getByFile("common.properties").get(key).toString();
        return decryptKey(key, returnValue);
    }

    public static String decryptKey(String key, String returnValue) {
        if (Objects.isNull(returnValue)) {
            return null;
        }
        if (returnValue.startsWith(">>>") && returnValue.endsWith("<<<")) {
            DecryptRequest request = new DecryptRequest();
            request.setEncryptedText(returnValue);
            returnValue = HttpEncryptionWrapper.decrypt(request);
        }
        return returnValue;
    }

}
