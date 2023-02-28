package com.emaf.service.filter;

import com.emaf.service.common.logging.AppLogger;
import com.emaf.service.common.utils.Utilities;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * AccessRequestFilter
 *
 * @author khal
 * @since 2021/10/03
 */
public class AccessRequestFilter implements Filter {

    /**
     * Do filter
     *
     * @param request
     * @param response
     * @param chain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        String uri = req.getRequestURI();

        if (!Utilities.isStaticResouces(uri)) {
            AppLogger.initMDC(req);
        }
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            if (res.getStatus() == HttpServletResponse.SC_OK) {
                res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            throw e;
        } finally {
            if (AppLogger.isAccessLoggerEnable(uri)) {
                AppLogger.access(startTime, (HttpServletRequest) request, res);
            }
        }
    }

}
