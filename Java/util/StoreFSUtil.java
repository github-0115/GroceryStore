package uyun.show.server.domain.util;

import uyun.show.server.domain.Constants;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class StoreFSUtil {

    /**
     * @param {[type]} [description] baseUrl string fs文件系统api url dir string
     *                 fs保存目录路径 srcPath string 源文件路径 tags string tag flag string
     *                 "true":覆盖上传 apikey string 租户身份key
     * @return (results interface { }, error)
     * @throws Exception
     */
    public static String UploadFile(String storeUrl, String dir, String fileName, String srcPath, String type, String flag, String apikey)
            throws Exception {
        apikey = Constants.STORE_FS_APIKEY;

        Map<String, String> headers = new HashMap<>();
        headers.put("tags", type);
        headers.put("flag", flag);
        headers.put("apikey", apikey);
        return HttpUtil.upload(storeUrl + URLEncoder.encode(URLEncoder.encode(dir, "UTF-8"), "UTF-8") + "/" + URLEncoder.encode(URLEncoder.encode(fileName, "UTF-8"), "UTF-8") + "?apikey=" + apikey, srcPath, headers);
    }
}
