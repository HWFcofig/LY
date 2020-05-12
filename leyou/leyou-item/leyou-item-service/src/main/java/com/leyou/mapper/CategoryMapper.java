package com.leyou.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author admin
 * @ClassName CategoryMapper
 * @date 2020/3/27
 * @Version 1.0
 **/
public interface CategoryMapper extends Mapper<Category>,SelectByIdListMapper<Category,Long> {
    /**
     * 根据品牌信息查询商品分类
     * @param bid
     * @return
     */
    @Select("SELECT * FROM tb_category WHERE id IN (SELECT category_id FROM tb_category_brand WHERE brand_id = #{bid})")
    List<Category> queryByBrandId(Long bid);

}
