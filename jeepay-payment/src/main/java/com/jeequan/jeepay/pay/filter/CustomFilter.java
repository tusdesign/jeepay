package com.jeequan.jeepay.pay.filter;


import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CustomFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        CustomHttpServletRequest request = new CustomHttpServletRequest((HttpServletRequest) servletRequest);
        //request.addHeader("header","瓜田李下");
        request.addHeader("X-API-KEY", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI0MGY5N2UyMi1iYmJjLTRkNDQtYTA2ZC1hYzY5NDUxZDY3ODciLCJpYXQiOjE2Mjk4ODUzNDIsImlzcyI6Im1haW5mbHV4LmF1dGgiLCJzdWIiOiJhbXkuamlhbmdAZW1haWwuY29tIiwiaXNzdWVyX2lkIjoiY2JiNWIxZDgtZmE2Zi00MGM1LTlkN2QtOTdmNzNlMDBkNzBmIiwidHlwZSI6Mn0.sBWISerstcHB3iBD-Mi3s-f68NX4lfS5BUycX1bgNh0");
        filterChain.doFilter(request, servletResponse);
    }
}