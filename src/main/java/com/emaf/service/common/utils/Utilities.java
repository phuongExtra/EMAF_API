package com.emaf.service.common.utils;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * CommonUtils
 *
 * @author khal
 * @since 2020/06/13
 */
public class Utilities {



    /**
     * Convert string
     * Ex: This is my code -> this-is-my-code
     *
     * @param str
     * @return
     */
    public static String convertStr(String str) {
        String result = "";
        if (!StringUtils.isEmpty(str) && str.contains(" ")) {
            String[] strArr = str.trim().replaceAll("\\s+", " ").toLowerCase().split(" ");
            result = Arrays.asList(strArr).stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("-"));
        }

        return result;
    }

    /**
     * Check if it is a static resources
     *
     * @param uri
     */
    public static boolean isStaticResouces(String uri) {
        return uri.startsWith("/dist/")
                || uri.endsWith(".js")
                || uri.endsWith(".css")
                || uri.endsWith(".png")
                || uri.endsWith(".jpg")
                || uri.endsWith(".jpeg")
                || uri.endsWith(".svg")
                || uri.endsWith(".ico")
                || uri.endsWith(".woff")
                || uri.endsWith(".woff2")
                || uri.endsWith(".ttf")
                || uri.endsWith(".map")
                || uri.endsWith(".webmanifest")
                || uri.endsWith("manifest.json")
                || uri.endsWith("favicon.ico");
    }

    /**
     * @author: VuongVT2
     * @since: 2021-10-06 5:30 PM
     * @description: 20210522 -> 22-05-2021 or 2021-05-22...
     */
    public static String getFDate(String value, String srcPattern, String destPattern) {
        LocalDate datetime = LocalDate.parse(value, DateTimeFormatter.ofPattern(srcPattern));
        return datetime.format(DateTimeFormatter.ofPattern(destPattern));
    }

}
