package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author admin
 * @ClassName LeyouItemServiceApplication
 * @date 2020/3/24
 * @Version 1.0
 **/
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.mapper")
public class LeyouItemApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouItemApplication.class);
    };
}
