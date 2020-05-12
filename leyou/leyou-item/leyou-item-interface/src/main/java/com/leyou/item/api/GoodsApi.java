package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface GoodsApi {

    /**
     * @param key 搜索关键词
     * @param saleable 是否为通用字段
     * @param page 当前页
     * @param rows 每页大小
     * @return 查看商品列表信息 PageResult
     */
    @GetMapping("spu/page")
    PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key",required = false)String key,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "page",defaultValue = "=1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows
    );

    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    @GetMapping("spu/detail/{spuId}")
    SpuDetail querySpuDetailBySpuId(@PathVariable("spuId")Long spuId);

    /**
     * 根据spuId查询sku的集合
     * @param spuId
     * @return
     */
    @GetMapping("sku/list")
    List<Sku> querySkusBySpuId(@RequestParam("id")Long spuId);

    @GetMapping("spu/{id}")
    public Spu querySpuById(@PathVariable("id")Long id);

    @GetMapping("sku/{skuId}")
    public Sku querySkuById(@PathVariable("skuId")Long skuId);
}
