package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author admin
 * @ClassName LeyouSearchService
 * @date 2020/4/18
 * @Version 1.0
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class LeyouSearchService {

    public static void main(String[] args) {
        SpringApplication.run(LeyouSearchService.class);
    };
}
