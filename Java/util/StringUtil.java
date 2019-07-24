package uyun.show.server.domain.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import uyun.show.server.domain.dto.RefreshData;
import uyun.show.server.domain.dto.TimeP;
import uyun.show.server.domain.dto.TimeValueParmar;

public class StringUtil {

    /**
     * 统计字符串中字符出现的次数
     *
     * @param s 字符串
     * @param c 待统计字符
     */
    public static int countChar(String s, char c) {
        if (s == null)
            return 0;
        else {
            int count = 0;
            for (int i = 0; i < s.length(); i++) {
                if (s.charAt(i) == c)
                    count++;
            }
            return count;
        }
    }

    public static String[] split(String src, String delimit) {
        if ((src == null) || (delimit == null))
            return null;
        List<String> stList = new ArrayList<>();
        int curPos = 0;
        int prePos = 0;
        int delimitLen = delimit.length();
        while (true) {
            curPos = src.indexOf(delimit, curPos);
            if (curPos < 0) {
                if (prePos > src.length())
                    break;
                stList.add(src.substring(prePos, src.length()));
                break;
            }

            if (curPos >= prePos) {
                stList.add(src.substring(prePos, curPos));
            }
            curPos += delimitLen;
            prePos = curPos;
        }
        String[] ret = new String[stList.size()];
        stList.toArray(ret);
        return ret;
    }

    /**
     * @param string 发短信的内容
     * @return 将.换成_
     * 去除https:和http:
     */
    public static String SMSMsgReplace(String string) {
        String tmp = "";
        if (string == null) {
            return tmp;
        }
        tmp = string.replaceAll("\\.", "_");
        tmp = tmp.replaceAll("https:", "");
        tmp = tmp.replaceAll("http:", "");
        return tmp;
    }

    public static String toStringOrEmpty(Object string) {
        if (string == null) {
            return "";
        }
        return string.toString();
    }

    /**
     * 获取指定字符串出现的次数
     *
     * @param srcText  源字符串
     * @param findText 要查找的字符串
     * @return
     */
    public static int appearNumber(String srcText, String findText) {
        int count = 0;
        Pattern p = Pattern.compile(findText);
        Matcher m = p.matcher(srcText);
        while (m.find()) {
            count++;
        }
        return count;
    }

    /**
     * 根据数据源的url和数据集的生成生成
     *
     * @param sourceUrl
     * @param setUrl
     * @return
     */
    public static String doAPiUrl(String sourceUrl, String setUrl, String startTime, String endTime, String recentTime) {
        if (!sourceUrl.isEmpty()) {
            if (sourceUrl.contains("?")) {
                int index = sourceUrl.indexOf("?");
                String prefix = sourceUrl.substring(0, index);
                String suffix = sourceUrl.substring(index + 1);
                if (setUrl.contains("?")) {
                    setUrl = prefix + "/" + setUrl + "&" + suffix;
                }
                return prefix + "/" + setUrl + "?" + suffix;
            } else {
                return sourceUrl + "/" + setUrl;
            }
        } else {
            return setUrl;
        }
    }

    public static String doMysql(String URL, String port, String databaseName, String userName, String password) {
        return userName + ":" + password + "@" + "(" + URL + ":" + port + ")" + "/" + databaseName;
    }

    public static String doOracle(String URL, String port, String databaseName, String userName, String password) {
        return userName + "/" + password + "@" + URL + ":" + port + "/" + databaseName;
    }

    public static long convert2Ms(String refreshTime) {
        if (refreshTime == null || refreshTime.isEmpty()) {
            return 0;
        }
        if (refreshTime.endsWith("s")) {
            int time = Integer.parseInt(refreshTime.substring(0, refreshTime.length() - 1)) * 1000;
            return time;
        }
        if (refreshTime.endsWith("m")) {
            int time = Integer.parseInt(refreshTime.substring(0, refreshTime.length() - 1)) * 60 * 1000;
            return time;
        }
        if (refreshTime.endsWith("h")) {
            int time = Integer.parseInt(refreshTime.substring(0, refreshTime.length() - 1)) * 60 * 60 * 1000;
            return time;
        }
        return 0;
    }

    public static String formatTime(long ms, int interval) {

        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;

        String strDay = day > 0 && day < 32 ? String.valueOf(day) : ""; // 天
        String strHour = hour > 0 && hour < 24 ? String.valueOf(hour) : "";// 小时
        String strMinute = minute > 0 && minute < 60 ? String.valueOf(minute) : "";// 分钟
        String strSecond = second > 0 && second < 60 ? String.valueOf(second) : "";// 秒

        String cron = "";
        if (!StringUtils.isEmpty(strSecond)) {
            cron = cron + (interval == 0 ? "0/" : strSecond + "/") + strSecond + " ";
        } else {
            cron = cron + "*" + " ";
        }
        if (!StringUtils.isEmpty(strMinute)) {
            cron = cron + (interval == 0 ? "0/" : strMinute + "/") + strMinute + " ";
            cron = cron.replace("*", "0");
        } else {
            cron = cron + "*" + " ";
        }
        if (!StringUtils.isEmpty(strHour)) {
            cron = cron + (interval == 0 ? "0/" : strHour + "/") + strHour + " ";
            cron = cron.replace("*", "0");
        } else {
            cron = cron + "*" + " ";
        }
        if (!StringUtils.isEmpty(strDay)) {
            if ("7".equals(strDay)) {
                cron = "0 0 0 ? * %s";
                cron = String.format(cron, getWeekDay());
            } else if ("30".equals(strDay)) {
                cron = "0 0 0 %s/30 * ?";
                cron = String.format(cron, getMonthDay());
            } else {
                cron = "0 0 0 1/%s * ?";
                cron = String.format(cron, strDay);
            }
        } else {
            cron = cron + "*" + " ";
            cron = cron + "* ? *";
        }
        return cron;
    }

    private static String getMonthDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        int dayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(dayOfMonth);
    }

    private static String getWeekDay() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(System.currentTimeMillis()));
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return String.valueOf(dayOfWeek);
    }

    public static TimeP timeSwitch(TimeValueParmar recent) {
        return null;
    }

}
