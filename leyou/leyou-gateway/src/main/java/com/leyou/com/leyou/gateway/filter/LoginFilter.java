package com.leyou.com.leyou.gateway.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author admin
 * @ClassName LoginFilter
 * @date 2020/5/7
 * @Version 1.0
 **/
@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private FilterProperties filterProperties;

    @Override
    public String filterType() {
        //登录之前拦截，拦截前方法
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        //获取白名单信息
        List<String> allowPaths = this.filterProperties.getAllowPaths();
        // 获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = context.getRequest();
        //获取url信息
        String url = request.getRequestURL().toString();
        for (String allowPath: allowPaths) {
            if (StringUtils.contains(url,allowPath)){
                //如果请求地址在白名单内，那么直接放行，不需要进行run方法进行拦截
                return false;
            }
        }
        //是否要进行拦截
        return true;
    }

    @Override
    public Object run() throws ZuulException {//编写拦截业务逻辑
        // 获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = context.getRequest();
        // 获取token
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        // 校验
        try {
            // 校验通过什么都不做，即放行
            JwtUtils.getInfoFromToken(token,this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            // 校验出现异常，返回401
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
