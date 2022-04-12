package com.flink.streaming.web.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author zhuhuipei
 * @Description:
 * @date 2020-07-06
 * @time 00:43
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {


    @Resource
    private LoginInterceptor loginInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册loginInterceptor拦截器
        InterceptorRegistration registration = registry.addInterceptor(loginInterceptor);
        //所有路径都被拦截
        registration.addPathPatterns("/**");
        //添加不拦截路径
        registration.excludePathPatterns(
                "/static/**",
                "/static/*",
                "/admin/index",
                "/admin/qrcode",
                "/api/login",
                "/api/logout",
                "/alarmCallback",
                "/ok",
                "/log/*",
                "/favicon.ico");
    }
}
