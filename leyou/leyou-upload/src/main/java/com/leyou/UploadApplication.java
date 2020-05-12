package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author admin
 * @ClassName UploadApplication
 * @date 2020/4/4
 * @Version 1.0
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class UploadApplication {
    public static void main(String[] args) {
        SpringApplication.run(UploadApplication.class);
    };
}
