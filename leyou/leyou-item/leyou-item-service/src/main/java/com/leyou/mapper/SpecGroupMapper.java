package com.leyou.mapper;

import com.leyou.item.pojo.SpecGroup;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;


/**
 * @author admin
 * @ClassName SpecGroupMapper
 * @date 2020/4/9
 * @Version 1.0
 **/
public interface SpecGroupMapper extends Mapper<SpecGroup> {
    @Update("update tb_spec_group SET cid =#{cid} ,`name` =#{name} WHERE id =#{id}")
    void updateById(@Param("id") Long id, @Param("cid") Long cid, @Param("name") String name);
}
