package com.leyou.controller;

import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author admin
 * @ClassName CategoryController
 * @date 2020/3/27
 * @Version 1.0
 **/
@Controller
@RequestMapping("category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据parentId查询子类目
     * @param pid
     * @return
     */
    @GetMapping("list")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid",
            defaultValue = "0")Long pid){

        if (pid == null|| pid < 0){
            // 响应400(参数不合法)，相当于ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                return ResponseEntity.badRequest().build();
        }
        List<Category> categories = this.categoryService.queryCategoriesByPid(pid);
        if (CollectionUtils.isEmpty(categories)){//isEmpty：为空或者null返回是ture
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据品牌信息查询商品分类
     * @param bid
     * @return
     */
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid")Long bid){
        List<Category> categories = this.categoryService.queryByBrandId(bid);
        if (categories == null || categories.size() < 1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(categories);
    }

    /**
     *  查询 商品分类名称
     * @param ids
     * @return
     */
    @GetMapping
    public ResponseEntity<List<String>> queryNameByIds(@RequestParam("ids")List<Long> ids){
        List<String> names = this.categoryService.queryNamesByIds(ids);
        if (names == null || names.size() < 1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(names);
    }

    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    @GetMapping("all/level")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam("id")Long id){
        List<Category> categories = this.categoryService.queryAllByCid3(id);
        if (categories == null || categories.size()< 1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(categories);
    }

}
