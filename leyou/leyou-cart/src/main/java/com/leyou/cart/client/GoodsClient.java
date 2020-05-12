package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author admin
 * @ClassName GoodsClient
 * @date 2020/5/9
 * @Version 1.0
 **/
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
