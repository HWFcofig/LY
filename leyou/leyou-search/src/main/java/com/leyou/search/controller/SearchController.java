package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author admin
 * @ClassName SearchController
 * @date 2020/4/24
 * @Version 1.0
 **/
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 搜索商品
     *
     * @param searchRequest
     * @return
     */
    @PostMapping("page")
    public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest searchRequest){
        PageResult<Goods> goodsPageResult = this.searchService.search(searchRequest);
        if (goodsPageResult == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(goodsPageResult);
    }
}
