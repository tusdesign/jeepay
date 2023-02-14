package com.jeequan.jeepay.pay.filter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class CustomHttpServletRequest extends HttpServletRequestWrapper {

    private Map<String, String> headers = new HashMap<>();

    public CustomHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);

        if (headers.containsKey(name)) {
            value = headers.get(name);
        }
        return value;
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        names.addAll(headers.keySet());

        return Collections.enumeration(names);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        List<String> list = Collections.list(super.getHeaders(name));

        if (headers.containsKey(name)) {
            list.add(headers.get(name));
        }
        return Collections.enumeration(list);
    }
}