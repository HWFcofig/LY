package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author admin
 * @ClassName SearchService
 * @date 2020/4/20
 * @Version 1.0
 **/
@Service
public class SearchService {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    //jackson工具
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Goods buildGoods(Spu spu) throws IOException {
        Goods goods = new Goods();

        //根据分类的id查询分类名称
        List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu
                .getCid3()));
        //根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //根据spiId查询所有的sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        //初始化一个价格集合，收集所有sku的价格
        List<Long> prices = new ArrayList<>();
        //收集sku的必要字段信息
        List<Map<String,Object>> skuMapList = new ArrayList<>();
        skus.forEach(sku -> {
            prices.add(sku.getPrice());

            Map<String,Object> map = new HashMap<>();
            map.put("id",sku.getId());
            map.put("title",sku.getTitle());
            map.put("price",sku.getPrice());
            //获取sku中的图片，数据库的图片可能是多张，多张是以“,”分割，所以也以逗号来切割返回图片数组，获取第一张图片
            map.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(map);
        });
        //根据spu中的cid3查询出所有的搜索规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);
        //根据spuId查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值进行反序列化
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {
        });
        //把特殊的规格参数值进行反序列化
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });
        Map<String,Object> specs = new HashMap<>();
        params.forEach(param->{
            //判断规格参数的类型，是否是通用的规格参数
            if (param.getGeneric()){
                //如果是通用规格参数，则从genericSpecMap里面获取规格参数值
                String value = genericSpecMap.get(param.getId()).toString();
                //判断是否是数值类型，如果是数值类型，应该返回一个区间
                if (param.getNumeric()){
                    value = chooseSegment(value,param);
                }
                specs.put(param.getName(),value);
            }else {
                //特殊的规格参数，则从specialSpecMap里面获取规格参数值
                List<Object> value = specialSpecMap.get(param.getId());
                specs.put(param.getName(),value);
            }
        });
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要分类名称以及品牌名称
        goods.setAll(spu.getTitle()+" "+ com.sun.deploy.util.StringUtils.join(names, " ")+" "+brand.getName());
        //获取spu下所有的sku的几个
        goods.setPrice(prices);
        //获取spu下所有的sku，并转化成json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuMapList));
        //获取所有查询的规格参数：{name:value}
        goods.setSpecs(specs);
        return goods;
    }

    private String chooseSegment(String value, SpecParam p) {
        double val = NumberUtils.toDouble(value);
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if(segs.length == 2){
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if(val >= begin && val < end){
                if(segs.length == 1){
                    result = segs[0] + p.getUnit() + "以上";
                }else if(begin == 0){
                    result = segs[1] + p.getUnit() + "以下";
                }else{
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 搜索商品
     * @param searchRequest
     * @return
     */
    public SearchResult search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        if (key == null || StringUtils.isBlank(key)){
            // 判断是否有搜索条件，如果没有，直接返回null。不允许搜索全部商品
            return null;
        }
        //构建查询条件
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        //MatchQueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        BoolQueryBuilder basicQuery = buildBooleanQueryBuilder(searchRequest);
        // 1、对key进行全文检索查询
        queryBuilder.withQuery(basicQuery);
        // 2、通过sourceFilter设置返回的结果字段,我们只需要id、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","skus","subTitle"},null));
        // 3、分页
        // 准备分页参数
        int page = searchRequest.getPage();
        int size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        // 4、查询，获取结果
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());
        //获取聚合结果集并解析
        List<Map<String,Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        //判断分类聚合的结果集大小，等于1则聚合,并且分类聚合不能为空集合
        List<Map<String,Object>> specs = null;
        if (categories.size() == 1){
            //对规格参数进行聚合
            specs = getParamAggResult((Long)categories.get(0).get("id"), basicQuery);
        }

        // 封装结果并返回
        return new SearchResult(goodsPage.getTotalElements(),goodsPage.getTotalPages(),goodsPage.getContent(),
                categories, brands,specs);
    }

    /**
     * 构建bool查询构建器
     * @param searchRequest
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest searchRequest) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all",searchRequest.getKey()).operator(Operator.AND));
        // 添加过滤条件
        if (CollectionUtils.isEmpty(searchRequest.getFilter())){
            return boolQueryBuilder;
        }
        for (Map.Entry<String, Object> entry : searchRequest.getFilter().entrySet()) {
            String key = entry.getKey();
            if (StringUtils.equals("品牌",key)){
                key = "brandId";
            }else if (StringUtils.equals("分类",key)){
                // 如果是“分类”，过滤字段名：cid3
                key = "cid3";
            }else { // 如果是规格参数名，过滤字段名：specs.key.keyword
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key,entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 根据查询条件聚合规格参数
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String,Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        // 创建自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        // 基于基本的查询条件，聚合规格参数
        queryBuilder.withQuery(basicQuery);
        // 查询要聚合的规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, cid, null, true);
        // 添加聚合
        params.forEach(param ->{
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() +
                    ".keyword"));
        });
        // 只需要聚合结果集，不需要查询结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{},null));
        // 执行聚合查询
        AggregatedPage<Goods> goodsPage = (AggregatedPage)this.goodsRepository.search(queryBuilder.build());
        // 定义一个集合，收集聚合结果集
        List<Map<String,Object>> paramMapList = new ArrayList<>();
        // 解析聚合查询的结果集
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            Map<String,Object> map = new HashMap<>();
            // 放入规格参数名
            map.put("k",entry.getKey());
            // 收集规格参数值
            List<Object> options = new ArrayList<>();
            // 解析每个聚合
            StringTerms terms = (StringTerms) entry.getValue();
            // 遍历每个聚合中桶，把桶中key放入收集规格参数的集合中
            terms.getBuckets().forEach(bucket -> options.add(bucket.getKeyAsString()));
            map.put("options",options);
            paramMapList.add(map);
        }
        return paramMapList;
    }

    /**
     * 解析品牌的聚合结果集
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        //获取聚合中的桶
        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }

    /**
     * 解析分类的聚合结果集
     * @param aggregation
     * @return
     */
    private List<Map<String,Object>> getCategoryAggResult(Aggregation aggregation) {
        LongTerms terms = (LongTerms) aggregation;
        return terms.getBuckets().stream().map(bucket -> {
            Map<String,Object> map = new HashMap<>();
            long id = bucket.getKeyAsNumber().longValue();
            List<String> names = this.categoryClient.queryNamesByIds(Arrays.asList(id));
            map.put("id",id);
            map.put("name",names.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 创建索引方法
     * @param id
     */
    public void createIndex(Long id) throws IOException {
        Spu spu = this.goodsClient.querySpuById(id);
        // 构建商品
        Goods goods = this.buildGoods(spu);
        // 保存数据到索引库
        Goods save = this.goodsRepository.save(goods);
    }

    /**
     * 删除索引方法
     * @param id
     */
    public void deleteIndex(Long id) {
        this.goodsRepository.deleteById(id);
    }
}
