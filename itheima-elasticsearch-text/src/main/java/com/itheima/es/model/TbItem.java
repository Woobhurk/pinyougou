package com.itheima.es.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Map;

/**
 * 测试POJO类
 *
 * 1.保证 能创建 索引  类型  文档，  文档的唯一标识
 *
 * 2。保证 创建映射  （是否索引 是否存储 是否 分词 分词器是什么 数据类型是什么）
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.itheima.es.model *
 * @since 1.0
 */
@Document(indexName = "pinyougou",type = "item")
public class TbItem implements Serializable {

    @Override
    public String toString() {
        return "TbItem{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", goodsId=" + goodsId +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", seller='" + seller + '\'' +
                '}';
    }

    @Id//用于表示文档的唯一标识  主键
    @Field(type = FieldType.Long) //如果是文档的唯一标识 数据类型 都会变成keyword
    private Long id;

    @Field(type = FieldType.Object)//指定一个对象的数据类型 用于动态的查询某一个属性的值的时候的时候用。
    private Map<String,String> specMap;

    public Map<String, String> getSpecMap() {
        return specMap;
    }

    public void setSpecMap(Map<String, String> specMap) {
        this.specMap = specMap;
    }

    @Field(index=true,analyzer = "ik_smart",searchAnalyzer = "ik_smart",type = FieldType.Text,copyTo = "keyword")
    private String title;//商品的标题
    @Field(type = FieldType.Long)
    private Long goodsId;
    @Field(type = FieldType.Keyword,copyTo = "keyword")//不分词
    private String category; //
    @Field(type = FieldType.Keyword,copyTo = "keyword")
    private String brand;
    @Field(type = FieldType.Keyword,copyTo = "keyword")
    private String seller;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }
}
