package com.jeequan.jeepay.pay.config;


import org.apache.tomcat.util.http.MimeHeaders;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 滤器实现对请求头的修改
 * @Author: chengzhengwen
 */
@WebFilter
public class AuthHeaderSettingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        //HeaderMapRequestWrapper headerMapRequestWrapper = new HeaderMapRequestWrapper(req);
        String token = req.getHeader("X-API-KEY");

        if (!StringUtils.isEmpty(token)) {
            //headerMapRequestWrapper.addHeader("loginId","456");
            Map<String, String> map = new HashMap<>();
            map.put("X-API-KEY", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI0MGY5N2UyMi1iYmJjLTRkNDQtYTA2ZC1hYzY5NDUxZDY3ODciLCJpYXQiOjE2Mjk4ODUzNDIsImlzcyI6Im1haW5mbHV4LmF1dGgiLCJzdWIiOiJhbXkuamlhbmdAZW1haWwuY29tIiwiaXNzdWVyX2lkIjoiY2JiNWIxZDgtZmE2Zi00MGM1LTlkN2QtOTdmNzNlMDBkNzBmIiwidHlwZSI6Mn0.sBWISerstcHB3iBD-Mi3s-f68NX4lfS5BUycX1bgNh0");
            modifyHeaders(map, req);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }


    private void modifyHeaders(Map<String, String> headerses, HttpServletRequest request) {
        if (headerses == null || headerses.isEmpty()) {
            return;
        }
        Class<? extends HttpServletRequest> requestClass = request.getClass();
        try {
            Field request1 = requestClass.getDeclaredField("request");
            request1.setAccessible(true);
            Object o = request1.get(request);
            Field coyoteRequest = o.getClass().getDeclaredField("coyoteRequest");
            coyoteRequest.setAccessible(true);
            Object o1 = coyoteRequest.get(o);
            Field headers = o1.getClass().getDeclaredField("headers");
            headers.setAccessible(true);
            MimeHeaders o2 = (MimeHeaders) headers.get(o1);
            for (Map.Entry<String, String> entry : headerses.entrySet()) {
                o2.removeHeader(entry.getKey());
                o2.addValue(entry.getKey()).setString(entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}