package com.jeequan.jeepay.pay.web;

import com.jeequan.jeepay.pay.filter.CustomFilter;
import com.jeequan.jeepay.pay.filter.RefundInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private CustomFilter customFilter;

    @Bean
    public FilterRegistrationBean<CustomFilter> initFilterRegistrationBean(){
        FilterRegistrationBean<CustomFilter> registrationBean=new FilterRegistrationBean<>();
        registrationBean.setFilter(customFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public RefundInterceptor myInterceptor(){
        return new RefundInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(myInterceptor()).addPathPatterns("/api/refund/refundOrder");
    }
}