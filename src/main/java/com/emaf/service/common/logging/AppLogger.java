package com.emaf.service.common.logging;

import com.emaf.service.common.utils.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AppLogger
 *
 * @author khal
 * @since 2021/10/02
 */
public class AppLogger {

    @Value("${spring.profiles.active}")
    private static String profilesActive;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static final String IP_KEY = "ip";

    private static final String USER_ID_KEY = "userId";

    private static final String UID_KEY = "uid";

    private static final String SESSION_ID_KEY = "sessionId";

    private static final String START_TIME_KEY = "startTime";

    private static final String REQUEST_PATH_KEY = "reqPath";

    private static final String PARAMS_QUERY_KEY = "params";

    private static final String PROCESS_TIME_KEY = "processTime";

    private static final String HTTP_GET = "GET";

    private static final String STATUS_KEY = "status";

    private static final String MESSAGE_KEY = "msg";

    private static final Logger ACCESS_LOGGER = LoggerFactory.getLogger(getLoggerName("accessLogger"));
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger(getLoggerName("errorLogger"));

    public static void initMDC(final HttpServletRequest request) {
        MDC.clear();

        // get user id
        String userId = request.getHeader("_user_id");
        userId = StringUtils.hasLength(userId) ? userId : "-";

        // get uid id
        String uid = request.getHeader("_uid");
        uid = StringUtils.hasLength(uid) ? uid : "-";

        // get user id
        String sessionId = request.getHeader("_session_id");
        sessionId = StringUtils.hasLength(sessionId) ? sessionId : "-";

        // put into mdc
        MDC.put(USER_ID_KEY, userId);
        MDC.put(UID_KEY, uid);
        MDC.put(SESSION_ID_KEY, sessionId);
        MDC.put(IP_KEY, request.getLocalAddr());
    }

    public static void accessLog(String message) {
        ACCESS_LOGGER.info(message);
    }

    public static boolean isAccessLoggerEnable(final String uri) {
        return ACCESS_LOGGER.isInfoEnabled() && StringUtils.hasLength(uri) && !Utilities.isStaticResouces(uri);
    }

    public static void access(final long startTime, final HttpServletRequest request, final HttpServletResponse response) {
        StringBuilder strBuff = new StringBuilder();

        SimpleDateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
        Date dt = new Date(startTime);

        // get query string
        String queryStr = request.getQueryString();
        queryStr = StringUtils.isEmpty(queryStr) ? "-" : queryStr;

        strBuff.append(START_TIME_KEY).append(":").append(format.format(dt))                            // start time request is sent
                .append("\t").append(IP_KEY).append(":").append(request.getLocalAddr())                 // ip
                .append("\t").append(REQUEST_PATH_KEY).append(":").append(request.getRequestURI())      // request uri
                .append("\t").append(PARAMS_QUERY_KEY).append(":").append(queryStr)                     // query param
                .append("\t").append(PROCESS_TIME_KEY).append(":");

        // calculate time on each request is HTTP GET has content type
        if (HTTP_GET.equals(request.getMethod()) && request.getContentType() != null) {
            double processTime = System.currentTimeMillis() - startTime;
            processTime = (processTime * 1.0) / (1000 * 1.0);
            strBuff.append(processTime);
        } else {
            strBuff.append("-");
        }

        strBuff.append("\t").append(STATUS_KEY).append(":").append(response.getStatus());

        // log access
        accessLog(strBuff.toString());
    }

    public static void errorLog(final String message, final Throwable throwable) {
        ERROR_LOGGER.error(msgBuild(message), throwable);
    }

    private static String msgBuild(final String message) {
        String msg = StringUtils.isEmpty(message) ? "-" : message;
        StringBuilder strBuff = new StringBuilder(MESSAGE_KEY);
        strBuff.append(":").append(msg);

        return strBuff.toString();
    }

    private static String getLoggerName(final String loggerName) {
        return loggerName + "_" + profilesActive;
    }

}
