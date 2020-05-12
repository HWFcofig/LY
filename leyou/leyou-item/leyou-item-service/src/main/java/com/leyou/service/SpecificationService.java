package com.leyou.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.mapper.SpecGroupMapper;
import com.leyou.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.List;

/**
 * @author admin
 * @ClassName SpecificationService
 * @date 2020/4/9
 * @Version 1.0
 **/
@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper specGroupMapper;

    @Autowired
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        return this.specGroupMapper.select(group);
    }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    public List<SpecParam> queryParams(Long gid,Long cid,Boolean generic,Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setGeneric(generic);
        param.setSearching(searching);
        return this.specParamMapper.select(param);
    }

    /**
     *
     * @param specGroup
     * @return  新增参数组
     */
    @Transactional
    public void saveGroup(SpecGroup specGroup) {
        this.specGroupMapper.insertSelective(specGroup);
    }

    /**
     *
     * @param specParam
     * @return 新增分组参数详情数据
     */
    @Transactional
    public void saveParam(SpecParam specParam) {
        SpecGroup specGroup = new SpecGroup();
        this.specParamMapper.insertSelective(specParam);
    }

    /**
     *  更新参数组
     * @param id
     * @param cid
     * @param name
     */
    @Transactional
    public void updateGroup(Long id, Long cid, String name) {
        this.specGroupMapper.updateById(id,cid,name);
    }

    /**
     *  更新分组参数详情数据
     * @param specParam
     */
    @Transactional
    public void updateParam(SpecParam specParam) {
        Example example = new Example(SpecParam.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",specParam.getId());
        this.specParamMapper.updateByExampleSelective(specParam,example);
    }


    /**
     *
     * @param id
     * @return  删除分组参数详情数据
     */
    @Transactional
    public void deleteParam(Long id) {
        this.specParamMapper.deleteByPrimaryKey(id);
    }

    /**
     *  删除参数组信息，如果参数组下有详情信息的，也要跟着情况详情信息
     * @param id
     */
    @Transactional
    public void deleteGroup(Long id) {
        this.specGroupMapper.deleteByPrimaryKey(id);
        SpecParam specParam = new SpecParam();
        Example example = new Example(SpecParam.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("groupId",id);
        //分组删除之后，该分组下的详情信息也要跟着自动清掉
        this.specParamMapper.deleteByExample(example);
    }

    public List<SpecGroup> querySpecsByCid(Long cid) {
        List<SpecGroup> specGroups = this.queryGroupsByCid(cid);
        specGroups.forEach(group->{
            List<SpecParam> params = this.queryParams(group.getId(), null, null, null);
            group.setParams(params);
        });
        return specGroups;
    }
}
