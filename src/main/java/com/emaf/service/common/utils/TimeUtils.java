package com.emaf.service.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * TimeUtils
 *
 * @author khal
 * @since 2022/01/11
 */
public class TimeUtils {

    /**
     * Datetime Formatter
     */
    public static final String DEFAULT_ZONE_ID = "Asia/Ho_Chi_Minh";
    public static final String DTF_dd_MM_yyyy_HH_mm_ss = "dd-MM-yyyy HH:mm:ss";
    public static final String DTF_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String DTF_yyyyMMddHHmm = "yyyyMMddHHmm";
    public static final String DTF_yyyyMMdd = "yyyyMMdd";

    /**
     * Datetime
     */
    public static LocalDateTime nowDatetime() {
        return ZonedDateTime.now(ZoneId.of(DEFAULT_ZONE_ID)).toLocalDateTime();
    }

    public static String comNowDatetime() {
        return nowDatetime().format(getDTF(DTF_yyyyMMddHHmmss));
    }

    public static String comNowYYYYMMddHHmm() {
        return nowDatetime().format(getDTF(DTF_yyyyMMddHHmm));
    }

    public static LocalDateTime getDateTime(String datetime, String pattern) {
        return LocalDateTime.parse(datetime, getDTF(pattern));
    }

    public static String convertDatetime(String datetime, String inPattern, String outPattern) {
        return LocalDateTime
                .parse(datetime, getDTF(inPattern))
                .format(getDTF(outPattern));
    }

    /**
     * Date
     */
    public static LocalDate nowDate() {
        return LocalDate.now();
    }

    public static String comNowDate() {
        return nowDate().format(getDTF(DTF_yyyyMMdd));
    }

    public static LocalDate getDate(String date, String pattern) {
        return LocalDate.parse(date, getDTF(pattern));
    }

    public static String convertDate(String date, String inPattern, String outPattern) {
        return LocalDate.parse(date, getDTF(inPattern))
                .format(getDTF(outPattern));
    }

    /**
     * Common
     */
    public static DateTimeFormatter getDTF(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of(DEFAULT_ZONE_ID));
    }

    public static Date convertStrToDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyyMMddHHmmss").parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
