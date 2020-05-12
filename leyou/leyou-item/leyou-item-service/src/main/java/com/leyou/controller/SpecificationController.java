package com.leyou.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

/**
 * @author admin
 * @ClassName SpecificationController
 * @date 2020/4/9
 * @Version 1.0
 **/
@Controller
@RequestMapping("spec")
public class SpecificationController {

    @Autowired
    private SpecificationService specificationService;

    /**
     * 根据分类id查询分组
     * @param cid
     * @return
     */
    @GetMapping("groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid){
        List<SpecGroup> specGroups = this.specificationService.queryGroupsByCid(cid);
        if (CollectionUtils.isEmpty(specGroups)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specGroups);
    }

    /**
     *RequestBody处理接受json对象
     * @param specGroup
     * @return  新增参数组
     */
    @PostMapping("group")
    public ResponseEntity<Void> saveGroup(SpecGroup specGroup){
        this.specificationService.saveGroup(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *  更新参数组
     * @param id
     * @param cid
     * @param name
     */
    @PutMapping("group")
    public ResponseEntity<Void> updateGroup(@RequestParam("id")Long id,
                                            @RequestParam("cid")Long cid,
                                            @RequestParam("name")String name){
        this.specificationService.updateGroup(id,cid,name);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *  删除参数组信息，如果参数组下有详情信息的，也要跟着情况详情信息
     * @param id
     */
    @DeleteMapping("group/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable("id")Long id){
        this.specificationService.deleteGroup(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @return
     */
    @GetMapping("params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(value = "gid",required = false) Long gid,
            @RequestParam(value = "cid",required = false) Long cid,
            @RequestParam(value = "generic",required = false) Boolean generic,
            @RequestParam(value = "searching",required = false) Boolean searching
    ){
        List<SpecParam> params = this.specificationService.queryParams(gid,cid,generic,searching);
        if (CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

    /**
     *
     * @param specParam
     * @return 新增分组参数详情数据
     */
    @PostMapping("param")
    public ResponseEntity<Void> saveParam(SpecParam specParam){
        this.specificationService.saveParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *  更新分组参数详情数据
     * @param specParam
     */
    @PutMapping("param")
    public ResponseEntity<Void> updateParam(SpecParam specParam){
        this.specificationService.updateParam(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     *
     * @param id
     * @return  删除分组参数详情数据
     */
    @DeleteMapping("param/{id}")
    public ResponseEntity<Void> deleteParam(@PathVariable("id")Long id){
        this.specificationService.deleteParam(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("group/param{cid}")
    public ResponseEntity<List<SpecGroup>> querySpecsByCid(@PathVariable("cid")Long cid){
        List<SpecGroup> params = this.specificationService.querySpecsByCid(cid);
        if (CollectionUtils.isEmpty(params)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(params);
    }

}