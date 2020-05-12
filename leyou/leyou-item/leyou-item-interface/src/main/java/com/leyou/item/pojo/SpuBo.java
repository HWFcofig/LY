package com.leyou.item.pojo;

import java.util.List;

/**
 * @author admin
 * @ClassName SpuBo--spu扩展实体，根据业务需求
 * @date 2020/4/10
 * @Version 1.0
 **/
public class SpuBo extends Spu{
    // 品牌名称
    private String bname;

    // 商品分类名称
    private String cname;

    // 商品详情
    SpuDetail spuDetail;

    // sku列表
    List<Sku> skus;

    public SpuDetail getSpuDetail() {
        return spuDetail;
    }

    public void setSpuDetail(SpuDetail spuDetail) {
        this.spuDetail = spuDetail;
    }

    public List<Sku> getSkus() {
        return skus;
    }

    public void setSkus(List<Sku> skus) {
        this.skus = skus;
    }

    public String getBname() {
        return bname;
    }

    public void setBname(String bname) {
        this.bname = bname;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }
}
