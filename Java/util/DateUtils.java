package uyun.show.server.domain.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.time.DayOfWeek.MONDAY;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

public class DateUtils {
    public static Logger logger = LoggerFactory.getLogger(DateUtils.class);
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_HOUR_MINUTE_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String YEAR_MONTH_FORMAT = "yyyy-MM";
    private static final String YEAR_FORMAT = "yyyy";

    private static final DateTimeFormatter FT_DATE_TIME = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    private static final DateTimeFormatter FT_DATE_HOUR_MINUTE = DateTimeFormatter.ofPattern(DATE_HOUR_MINUTE_FORMAT);
    private static final DateTimeFormatter FT_DATE = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private static final DateTimeFormatter FT_YEAR_MONTH = DateTimeFormatter.ofPattern(YEAR_MONTH_FORMAT);
    private static final DateTimeFormatter FT_YEAR = DateTimeFormatter.ofPattern(YEAR_FORMAT);

    public static String currentDate() {
        return FT_DATE.format(LocalDate.now());
    }

    public static String currentDateTime() {
        return FT_DATE_TIME.format(LocalDateTime.now());
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return FT_DATE.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public static String formatDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return FT_DATE_TIME.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public static Date parse(String dateTime) {
        SimpleDateFormat sdf;
        if (dateTime.contains("CST")) {
            sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        } else {
            return parseDateTime(dateTime);
        }
        try {
            Date date = sdf.parse(dateTime);
            return date;
        } catch (ParseException e) {
            logger.error("Date parse error:" + e.getMessage());
        }
        return null;
    }

    public static String formatYear(Date date) {
        if (date == null) {
            return null;
        }
        return FT_YEAR.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public static String formatYearMonth(Date date) {
        if (date == null) {
            return null;
        }
        return FT_YEAR_MONTH.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public static String formatDateHourMinute(Date date) {
        if (date == null) {
            return null;
        }
        return FT_DATE_HOUR_MINUTE.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()));
    }

    public static Date getDateDayByDiff(Date date, Integer diff) {
        if (date == null) {
            return null;
        }
        return Date.from(date.toInstant().plus(diff, DAYS));
    }

    public static Date parseDate(String date) {
        try {
            return parseDate(LocalDate.from(FT_DATE.parse(date)));
        } catch (Exception e) {
            logger.error("Date parse error:" + e.getMessage());
        }
        return null;
    }

    public static Date parseDateTime(String date) {
        try {
            return parseDateTime(LocalDateTime.from(FT_DATE_TIME.parse(date)));
        } catch (Exception e) {
            logger.error("DateTime parse error:" + e.getMessage());
        }
        return null;
    }

    public static Date parseDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static Date parseDateTime(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    public static String getToday() {
        return formatDateTime(parseDateTime(LocalDateTime.now().truncatedTo(DAYS)));
    }

    public static String getWeekAgo() {
        return getDayBefore(7);
    }

    public static String getLastWeekStart(long time) {
        LocalDateTime localDateTime = time > 0 ? getDateTimeOfTimestamp(time) : LocalDateTime.now();
        return formatDateTime(parseDateTime(localDateTime.minusWeeks(1).with(MONDAY).with(LocalTime.MIN)));
    }

    public static String getLastWeekEnd(long time) {
        LocalDateTime localDateTime = time > 0 ? getDateTimeOfTimestamp(time) : LocalDateTime.now();
        return formatDateTime(parseDateTime(localDateTime.minusWeeks(1).with(MONDAY).plusDays(6).with(LocalTime.MAX)));
    }

    public static String getLastMonthStart(long time) {
        LocalDateTime localDateTime = time > 0 ? getDateTimeOfTimestamp(time) : LocalDateTime.now();
        return formatDateTime(parseDateTime(localDateTime.minusMonths(1).with(firstDayOfMonth()).with(LocalTime.MIN)));
    }

    public static String getLastMonthEnd(long time) {
        LocalDateTime localDateTime = time > 0 ? getDateTimeOfTimestamp(time) : LocalDateTime.now();
        LocalDateTime start = localDateTime.minusMonths(1);
        LocalDateTime end = start.with(lastDayOfMonth());
        return formatDateTime(parseDateTime(end.truncatedTo(DAYS).with(LocalTime.MAX)));
    }

    public static String getYesterDayStart(long time) {
        LocalDateTime localDateTime = time > 0 ? getDateTimeOfTimestamp(time) : LocalDateTime.now();
        return formatDateTime(parseDateTime(localDateTime.minusDays(1).with(LocalTime.MIN)));
    }

    public static String getYesterDayEnd(long time) {
        LocalDateTime localDateTime = time > 0 ? getDateTimeOfTimestamp(time) : LocalDateTime.now();
        return formatDateTime(parseDateTime(localDateTime.minusDays(1).with(LocalTime.MAX)));
    }


    public static String getDayBefore(int interval) {
        return formatDateTime(parseDateTime(LocalDateTime.now().minusDays(interval)));
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            logger.error("Date parse stamp error:" + e.getMessage());
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    public static String getTodayStamp(long time) {
        LocalDateTime localDateTime = time > 0 ? getDateTimeOfTimestamp(time) : LocalDateTime.now();
        return dateToStamp(formatDateTime(parseDateTime(localDateTime.truncatedTo(SECONDS))));
    }

    private static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    public static String getLastStart(long time, String s) {
        switch (s) {
            case "86400000":
                return dateToStamp(getYesterDayStart(time));
            case "604800000":
                return dateToStamp(getLastWeekStart(time));
            case "2592000000":
                return dateToStamp(getLastMonthStart(time));
            default:
                return "";
        }
    }

    public static String getLastEnd(long time, String s) {
        switch (s) {
            case "86400000":
                return dateToStamp(getYesterDayEnd(time));
            case "604800000":
                return dateToStamp(getLastWeekEnd(time));
            case "2592000000":
                return dateToStamp(getLastMonthEnd(time));
            default:
                return "";
        }
    }

    public static long StringToLong(String s) {
        String prefix = s.substring(0, s.length() - 1);
        String suffix = s.substring(s.length() - 1, s.length());
        int num = Integer.parseInt(prefix);
        switch (suffix.toLowerCase()) {
            case "d":
                return num * (24 * 60 * 60 * 1000);
            case "h":
                return num * (60 * 60 * 1000);
            case "m":
                return num * (60 * 1000);
            case "s":
                return num * 1000;
        }
        return 0;
    }

    public static void main(String[] args) {
        System.out.println(getLastWeekStart(0));
        System.out.println(getLastWeekEnd(0));
        System.out.println(getLastMonthStart(0));
        System.out.println(getLastMonthEnd(0));
        System.out.println(getYesterDayStart(0));
        System.out.println(getYesterDayEnd(0));
        System.out.println(getWeekAgo());
        System.out.println(getToday());
    }
}