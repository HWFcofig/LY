package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author admin
 * @ClassName GoodsRepository
 * @date 2020/4/20
 * @Version 1.0
 **/
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
