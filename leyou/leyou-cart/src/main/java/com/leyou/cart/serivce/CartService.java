package com.leyou.cart.serivce;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 * @ClassName CartService
 * @date 2020/5/9
 * @Version 1.0
 **/
@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;

    private static final String KEY_PREFIX = "user:cart";

    public void addCart(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        //查询购物车记录
        BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate
                .boundHashOps(KEY_PREFIX + userInfo.getId());
        String key = cart.getSkuId().toString();
        Integer num = cart.getNum();
        //判断当前的商品是否存在购物车中
        if (boundHashOps.hasKey(key)){
            //在，更新数量
            String cartJson = boundHashOps.get(key).toString();
            //反序列化json对象
            cart = JsonUtils.parse(cartJson, Cart.class);
            cart.setNum(cart.getNum() + num);
        }else {
            //查询sku对象信息
            Sku sku = this.goodsClient.querySkuById(cart.getSkuId());
            //不在。新增购物车
            cart.setTitle(sku.getTitle());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(),",")[0]);
            cart.setPrice(sku.getPrice());
            cart.setUserId(userInfo.getId());
        }
        //序列化json
        boundHashOps.put(key,JsonUtils.serialize(cart));
    }

    public List<Cart> queryCartList() {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + userInfo.getId();
        //判断用户是否又购物车记录
        if (!this.redisTemplate.hasKey(key)){
            return null;
        }
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate
                .boundHashOps(key);

        List<Object> cartlist = boundHashOps.values();

        //如果购物车集合为空，那么直接返回一个null
        if (CollectionUtils.isEmpty(cartlist)){
            return null;
        }
        //把list<Object>转化为list<Cart>集合
        return cartlist.stream().map(cartJson -> JsonUtils.parse(cartJson.toString(),Cart.class)).collect(Collectors
                .toList());

    }

    public void updateNum(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + userInfo.getId();
        //判断用户是否又购物车记录
        if (!this.redisTemplate.hasKey(key)){
            return ;
        }
        Integer num = cart.getNum();
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate
                .boundHashOps(key);
        String cartJson = boundHashOps.get(cart.getSkuId().toString()).toString();
        //反序列化购物车信息
        cart = JsonUtils.parse(cartJson,Cart.class);
        //更新数量
        cart.setNum(num);
        boundHashOps.put(cart.getSkuId().toString(),JsonUtils.serialize(cart));
    }

    public void deleteCart(String skuId) {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        String key = KEY_PREFIX + userInfo.getId();
        //获取用户的购物车记录
        BoundHashOperations<String, Object, Object> boundHashOps = this.redisTemplate
                .boundHashOps(key);
        boundHashOps.delete(skuId);
    }
}
