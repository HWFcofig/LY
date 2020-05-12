package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author admin
 * @ClassName UserApi
 * @date 2020/5/6
 * @Version 1.0
 **/
public interface UserApi {

    @GetMapping("query")
    public User queryUser(@RequestParam("username")String username, @RequestParam("password")String
            password);
}
