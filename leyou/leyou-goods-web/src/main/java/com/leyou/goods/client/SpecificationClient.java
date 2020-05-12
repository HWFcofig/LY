package com.leyou.goods.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 *  规格参数的FeignClient
 */
@FeignClient("item-service")
public interface SpecificationClient extends SpecificationApi {
}