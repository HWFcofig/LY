package com.leyou.user.service;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author admin
 * @ClassName UserService
 * @date 2020/5/3
 * @Version 1.0
 **/
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    static final String KEY_PREFIX = "user:code:phone:";

    public Boolean checkUser(String data, Integer type) {

        User record = new User();
        if (type == 1){
            record.setUsername(data);
        }else if (type == 2){
            record.setPhone(data);
        }else {
            return null;
        }
        return this.userMapper.selectCount(record) == 0;
    }

    //生成短信验证码
    public void sendVerifyCode(String phone) {
        //生成验证码
        String code = NumberUtils.generateCode(6);
        //发送短信
        HashMap<String, String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);
        amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE","sms.verify.code",msg);
        //将code放入redis中
        this.redisTemplate.opsForValue().set(KEY_PREFIX + phone,code,5,TimeUnit.MINUTES);
    }

    //用户注册
    public void register(User user, String code) {
        //从redis中获取存储得验证码
        String rediscode = this.redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //校验短信验证码
        if (!StringUtils.equals(code,rediscode)){
            return;
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(),salt));
        //新增用户
        user.setId(null);
        user.setCreated(new Date());
        this.userMapper.insertSelective(user);
    }

    public User queryUser(String username, String password) {
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        //判断user是否为空
        if (user == null){
            return null;
        }
        //对密码加盐加密
        password = CodecUtils.md5Hex(password,user.getSalt());
        //从数据库中获取密码
        if (StringUtils.equals(password,user.getPassword())){
            return user;
        }
        return null;
    }
}
