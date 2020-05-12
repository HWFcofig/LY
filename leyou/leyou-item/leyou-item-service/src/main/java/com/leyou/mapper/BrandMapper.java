package com.leyou.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author admin
 * @ClassName BrandMapper
 * @date 2020/3/30
 * @Version 1.0
 **/
public interface BrandMapper extends Mapper<Brand> {
    @Insert("INSERT INTO tb_category_brand(category_id, brand_id) VALUES (#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid,@Param("bid") Long bid);

    @Delete("delete from tb_category_brand where brand_id=#{bid}")
    void deleteCategoryAndBrandCid(@Param("bid")Long bid);

    @Select("SELECT b.* from tb_brand b INNER JOIN tb_category_brand cb on b.id=cb.brand_id where cb.category_id=#{cid}")
    List<Brand> selectBrandByCid(@Param("cid")Long cid);
}
