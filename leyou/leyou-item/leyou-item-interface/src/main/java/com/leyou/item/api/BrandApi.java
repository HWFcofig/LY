package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("brand")
public interface BrandApi {

    /**
     * 根据id查询__品牌名称
     * @return
     */
    @GetMapping("{id}")
    Brand queryBrandById(@PathVariable("id")Long id);

}
