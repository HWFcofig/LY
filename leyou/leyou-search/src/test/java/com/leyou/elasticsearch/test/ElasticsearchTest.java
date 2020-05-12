package com.leyou.elasticsearch.test;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuBo;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 * @ClassName ElasticsearchTest
 * @date 2020/4/20
 * @Version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private BrandClient brandClient;

    @Test
    public void createIndex(){
        //创建索引
        this.elasticsearchTemplate.createIndex(Goods.class);
        //配置映射
        this.elasticsearchTemplate.putMapping(Goods.class);
        Integer page = 1;
        Integer rows = 100;
        do {
        //分页查询spu，获取分页结果集
        PageResult<SpuBo> result = this.goodsClient.querySpuBoByPage(null, null, page, rows);
        //获取当前页的数据
        List<SpuBo> items = result.getItems();
        //处理List<SpuBo>==>List<Goods>
        List<Goods> goodsList = items.stream().map(spuBo -> {
            try {
                return this.searchService.buildGoods(spuBo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(Collectors.toList());
        //执行新增数据的方法
        this.goodsRepository.saveAll(goodsList);

        rows = items.size();
        page++;
        }while (rows == 100);
    }

    @Test
    public void testQueryCategories() {
        Brand brand = this.brandClient.queryBrandById(1528l);
        System.out.println("-----------------------");
        System.out.println(brand.getName());
        System.out.println("-----------------------");
    }
}
