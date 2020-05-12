package com.leyou.service;

import com.leyou.item.pojo.Category;
import com.leyou.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author admin
 * @ClassName CategoryService
 * @date 2020/3/27
 * @Version 1.0
 **/
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据parentId查询子类目
     * @param pid
     * @return
     */
    public List<Category> queryCategoriesByPid(Long pid){
        Category record = new Category();
        record.setParentId(pid);
        return categoryMapper.select(record);
    }

    /**
     * 根据品牌信息查询商品分类
     * @param bid
     * @return
     */
    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }

    /**
     *
     * @param ids
     * @return  查询名称的功能
     */
    public List<String> queryNamesByIds(List<Long> ids){
        List<Category> categories = this.categoryMapper.selectByIdList(ids);
        List<String> names = new ArrayList<>();
        for (Category category: categories) {
            names.add(category.getName());
        }
        return names;
//        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }

    /**
     * 根据3级分类id，查询1~3级的分类
     * @param id
     * @return
     */
    public List<Category> queryAllByCid3(Long id) {
        Category cid3 = this.categoryMapper.selectByPrimaryKey(id);
        Category cid2 = this.categoryMapper.selectByPrimaryKey(cid3.getParentId());
        Category cid1 = this.categoryMapper.selectByPrimaryKey(cid2.getParentId());
        return Arrays.asList(cid1,cid2,cid3);
    }
}
