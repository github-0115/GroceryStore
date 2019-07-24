package uyun.show.server.domain.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpUtil {
    public IpUtil() {
    }

    public static String getLocalIp() {
        try {
            Enumeration en = NetworkInterface.getNetworkInterfaces();

            while(en.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface)en.nextElement();
                Enumeration enumInetAddr = ni.getInetAddresses();

                while(enumInetAddr.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress)enumInetAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException var4) {
        }

        return null;
    }
}
