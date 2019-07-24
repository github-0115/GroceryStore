package uyun.show.server.domain.util;

import java.util.UUID;

/**
 * @author weixy
 * @create 2018年11月8日
 */
public class UUIDUtil {

    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

//    public static String randomUUID(int lenght) {
//        return UUID.randomUUID().toString();
//    }
}
