package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author admin
 * @ClassName LeyouCartApplication
 * @date 2020/5/7
 * @Version 1.0
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LeyouCartApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouCartApplication.class);
    };
}
