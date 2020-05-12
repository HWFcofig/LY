package com.leyou.cart.config;

import com.leyou.cart.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author admin
 * @ClassName LeyouWebMvcConfiguration
 * @date 2020/5/9
 * @Version 1.0
 **/
@Configuration
public class LeyouWebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 添加Spring MVC生命周期拦截器的预处理和后处理
     * 控制器方法调用。拦截器可以注册应用
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
