package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.mapper.BrandMapper;
import com.leyou.mapper.CategoryMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author admin
 * @ClassName BrandService
 * @date 2020/3/30
 * @Version 1.0
 **/
@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     *
     * @param key   搜索关键词，String
     * @param page  当前页，int
     * @param rows  每页大小，int
     * @param sortBy    排序字段，String
     * @param desc  是否为降序，boolean
     * @return
     */
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        //初始化example对象
        Example example = new Example(Brand.class);
        //添加查询条件
        Example.Criteria criteria = example.createCriteria();
        // 根据name模糊查询，或者根据首字母查询
        if (StringUtils.isNotBlank(key)){
            criteria.andLike("name","%" + key + "%").orEqualTo("letter",key);
        }
        // 添加分页条件
        PageHelper.startPage(page,rows);
        // 添加排序条件
        if (StringUtils.isNotBlank(sortBy)){
            example.setOrderByClause(sortBy + " " + (desc?"asc":"desc"));
        }
        List<Brand> brands = this.brandMapper.selectByExample(example);
        // 包装成pageInfo
        PageInfo<Brand> brandPageInfo = new PageInfo<>(brands);
        return new PageResult<>(brandPageInfo.getTotal(),brandPageInfo.getList());
    }

    /**
     * 新增品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //先新增brand
        this.brandMapper.insertSelective(brand);
        //再新增中间表
        cids.forEach(cid->{
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });
    }

    /**
     * 更新品牌
     * @param brand
     * @param cids
     */
    @Transactional
    public void updateBrand(Brand brand, List<Long> cids) {
        //初始化example对象
        Example brandexample = new Example(Brand.class);
        //添加条件
        Example.Criteria brandcriteria = brandexample.createCriteria();
        brandcriteria.andEqualTo("id",brand.getId());
        //先更新brand
        this.brandMapper.updateByExampleSelective(brand,brandexample);
        //清空中间表cid
        this.brandMapper.deleteCategoryAndBrandCid(brand.getId());
        //更新中间表
        cids.forEach(cid->{
            this.brandMapper.insertCategoryAndBrand(cid,brand.getId());
        });
    }

    /**
     * 删除品牌
     * @param bid
     * @return
     */
    @Transactional
    public void deleteBrand(Long bid) {
        //根据bid去清空中间表信息
        this.brandMapper.deleteCategoryAndBrandCid(bid);
        //根据主键删除品牌表数据
        this.brandMapper.deleteByPrimaryKey(bid);
    }

    /**
     *  商品列表中的商品规格参数回显
     * @param cid
     * @return
     */
    public List<Brand> queryBrandsByCid(Long cid) {
        return this.brandMapper.selectBrandByCid(cid);
    }

    /**
     * 根据id查询__品牌名称
     * @return
     */
    public Brand queryBrandById(Long id) {
        return this.brandMapper.selectByPrimaryKey(id);
    }
}
