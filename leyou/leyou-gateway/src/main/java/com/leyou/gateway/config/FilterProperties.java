package com.leyou.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.util.List;

/**
 * @author admin
 * @ClassName FilterProperties  配置白名单
 * @date 2020/5/7
 * @Version 1.0
 **/
@ConfigurationProperties(prefix = "leyou.filter")
public class FilterProperties {

    private List<String> allowPaths;

    public List<String> getAllowPaths() {
        return allowPaths;
    }

    public void setAllowPaths(List<String> allowPaths) {
        this.allowPaths = allowPaths;
    }
}
