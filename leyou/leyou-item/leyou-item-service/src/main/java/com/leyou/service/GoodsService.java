package com.leyou.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.mapper.*;
import com.sun.deploy.util.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author admin
 * @ClassName GoodsService
 * @date 2020/4/10
 * @Version 1.0
 **/
@Service
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * @param key 搜索关键词
     * @param saleable 是否为通用字段
     * @param page 当前页
     * @param rows 每页大小
     * @return 查看商品列表信息 PageResult
     */
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //添加查询条件
        if (StringUtil.isNotEmpty(key)){
            criteria.andLike("title","%" + key +"%");
        }
        if (saleable != null){
            criteria.andEqualTo("saleable",saleable);
        }
        //添加上下架的过滤条件
        //添加分页
        PageHelper.startPage(page,rows);
        //执行查询，获取spu集合
        List<Spu> spus = this.spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        ArrayList<SpuBo> spuBos = new ArrayList<>();
        //spu集合转化成spubo集合
       spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            // copy共同属性的值到新的对象
            BeanUtils.copyProperties(spu,spuBo);
            // 查询分类名称
            List<String> names = this.categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(),
                    spu.getCid3()));
            //StringUtils.join:转换成一种格式，例如：XXX/XXX
            spuBo.setCname(StringUtils.join(names,"/"));
            //查询品牌信息
            spuBo.setBname(this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            spuBos.add(spuBo);
        });
        //返回PageResult<SpuBo>
        return new PageResult<>(pageInfo.getTotal(), spuBos);
    }

    /**
     * 新增商品
     * @param spuBo
     */
    public void saveGoods(SpuBo spuBo) {
        //设置默认字段
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        // 新增spu
        this.spuMapper.insertSelective(spuBo);

        // 新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.spuDetailMapper.insertSelective(spuDetail);

        //因为skus是一个list集合，所以需要对它遍历新增进去
        saveSkuAndStock(spuBo);

        sendMessage("insert",spuBo.getId());

    }

    private void sendMessage(String type,Long id) {
        try {
            this.amqpTemplate.convertAndSend("item." + type,id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    /**
     *  新增sku和stock
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            // 新增sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.skuMapper.insertSelective(sku);

            // 新增库存
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            this.stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询sku的集合
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
            sku.setStock(stock.getStock());
        });
        return skus;
    }

    @Transactional
    public void updateGoods(SpuBo spuBo) {
        // 查询以前sku
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = this.skuMapper.select(record);
        skus.forEach(sku -> {
            // 删除stock
            this.stockMapper.deleteByPrimaryKey(sku.getId());
        });
        //删除sku
        Sku s = new Sku();
        s.setSpuId(spuBo.getId());
        this.skuMapper.delete(s);
        //新增sku和stock
        this.saveSkuAndStock(spuBo);
        //更新spu和spuDetail
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSubTitle(null);
        this.spuMapper.updateByPrimaryKeySelective(spuBo);
        this.spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        sendMessage("update",spuBo.getId());
    }

    public Spu querySpuById(Long id) {
        return this.spuMapper.selectByPrimaryKey(id);
    }

    public Sku querySkuById(Long skuId) {
        return this.skuMapper.selectByPrimaryKey(skuId);
    }
}
