package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author admin
 * @ClassName UserClient
 * @date 2020/5/6
 * @Version 1.0
 **/
@FeignClient(value = "user-service")
public interface UserClient extends UserApi {
}
