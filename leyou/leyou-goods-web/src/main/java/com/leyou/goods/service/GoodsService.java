package com.leyou.goods.service;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author admin
 * @ClassName GoodsService
 * @date 2020/4/28
 * @Version 1.0
 **/
@Service
public class GoodsService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    /**
     * 跳转到商品详情页
     * @param spuId
     * @return
     */
    public Map<String,Object> loadData(Long spuId){

        Map<String,Object> model = new HashMap<>();

        //获取spu
        Spu spu = this.goodsClient.querySpuById(spuId);
        //获取spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);
        //获取categories
        List<Long> cids = Arrays.asList(spu.getCid1(),spu.getCid2(),spu.getCid3());
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        List<Map<String,Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size() ; i++) {
            Map<String,Object> map = new HashMap<>();
            map.put("id",cids.get(i));
            map.put("name",names.get(i));
            categories.add(map);
        }
        //获取brand
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //获取skus
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spuId);
        //获取groups
        List<SpecGroup> groups = this.specificationClient.querySpecsByCid(spu.getCid3());
        //获取特殊的规格参数paramMap
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), false, null);
        Map<Long,String> paramMap = new HashMap<>();
        params.forEach(paeam ->{
            paramMap.put(paeam.getId(),paeam.getName());
        });

        model.put("spu",spu);
        model.put("spuDetail",spuDetail);
        model.put("categories",categories);
        model.put("brand",brand);
        model.put("skus",skus);
        model.put("groups",groups);
        model.put("paramMap",paramMap);

        return model;
    }

}
