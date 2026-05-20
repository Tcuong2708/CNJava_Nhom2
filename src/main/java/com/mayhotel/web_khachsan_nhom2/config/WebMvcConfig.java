package com.mayhotel.web_khachsan_nhom2.config;

import com.mayhotel.web_khachsan_nhom2.interceptor.AdminSecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AdminSecurityInterceptor adminSecurityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminSecurityInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/css/**", "/js/**", "/images/**", "/vendor/**", "/review/**");
    }
}