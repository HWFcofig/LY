package com.leyou.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author admin
 * @ClassName LeyouCorsConfig
 * @date 2020/3/30
 * @Version 1.0
 **/
@Configuration
public class LeyouCorsConfig {

    @Bean
    public CorsFilter corsFilter(){
        //初始化cors对象
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //添加配置信息----*的话，是所有请求都可以
        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");
        corsConfiguration.addAllowedOrigin("http://www.leyou.com");
        //设置cookie信息，如果进行设置了，请求就不能设置*了
        corsConfiguration.setAllowCredentials(true);
        //设置头部信息
        corsConfiguration.addAllowedHeader("*");
        //设置请求方法（请求方式：*都是代表所有GET\POST\PUT..）
        corsConfiguration.addAllowedMethod("*");
        //2.添加映射路径，我们拦截一切请求
        UrlBasedCorsConfigurationSource corsConfigurationSource = new
                UrlBasedCorsConfigurationSource();
        //添加放行路径和条件
        corsConfigurationSource.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsFilter(corsConfigurationSource);
    }


}
