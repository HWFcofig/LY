package com.leyou.goods.controller;

import com.leyou.goods.service.GoodsHtmlService;
import com.leyou.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author admin
 * @ClassName GoodsController
 * @date 2020/4/28
 * @Version 1.0
 **/
@Controller
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    /**
     * 跳转到商品详情页
     * @param model
     * @param spuId
     * @return
     */
    @GetMapping("item/{spuId}.html")
    public String toItemPage(@PathVariable("spuId")Long spuId, Model model){
        // 加载所需的数据
        Map<String, Object> map = this.goodsService.loadData(spuId);
        // 放入模型
        model.addAllAttributes(map);

        // 页面静态化
        this.goodsHtmlService.asyncExcute(spuId);

        return "item";
    }

}
