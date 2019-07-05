package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {

        Map<String, Object> resultMap = new HashMap<>();

        //1.获取关键字
        String keywords = (String) searchMap.get("keywords");

        //2.创建查询对象的构建对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();

        String category = null;
        if (keywords != null && !"".equals(keywords)) {
            //3.设置查询的条件
            //ativeSearchQueryBuilder.withIndices("pinyougou") 指定索引 默认查询所有的索引
            //nativeSearchQueryBuilder.withTypes("item") 指定类型 默认 查询所有的类型

//        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("keyword",keywords));
            nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(keywords, "seller", "category", "brand", "title"));

            //设置一个聚合查询的条件 ：1.设置聚合查询的名称（别名）2.设置分组的字段
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("category_group").field("category").size(50));


            //3.1 设置高亮显示的域（字段） 设置 前缀 和后缀
            nativeSearchQueryBuilder
                    .withHighlightFields(new HighlightBuilder.Field("title"))
                    .withHighlightBuilder(new HighlightBuilder().preTags("<em style=\"color:red\">").postTags("</em>"));


            //3.2 过滤查询  ----商品分类的过滤查询 选项面板上可能会被点击多次,所以要使用多条件组合查询
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

            category = (String) searchMap.get("category");
            //如果搜索关键字有,但是没有点击面板 category就为空 需要加上非空判断
            if (StringUtils.isNotBlank(category)) {
                //过滤子条件
                boolQueryBuilder.filter(QueryBuilders.termQuery("category", category));
            }

            //3.3 过滤查询 ----商品的品牌的过滤查询
            String brand = (String) searchMap.get("brand");
            //如果搜索关键字有,但是没有点击面板 category就为空 需要加上非空判断
            if (StringUtils.isNotBlank(brand)) {
                //过滤子条件 整体不分词 使用词条查询
                boolQueryBuilder.filter(QueryBuilders.termQuery("brand", brand));
            }

            //3.4 过滤查询 ----规格的过滤查询 获取到规格的名称 和规格的值  执行过滤查询
            Map<String, String> spec = (Map<String, String>) searchMap.get("spec");//获取页面传过来的{"网络":"移动4G","机身内存":"16G"} 对象
            if (spec != null) {
                //遍历规格集合
                for (String key : spec.keySet()) {
                    //过滤子条件
                    //使用对象域 object域搜索格式
                    //通过点击的规格选项 从索引库里面的specMap+传入的点击的规格字段对比 过滤掉不包含的数据
                    // 传入key 和  值                                           spec.网络.keyword            移动4G
                    boolQueryBuilder.filter(QueryBuilders.termQuery("specMap." + key + ".keyword", spec.get(key)));

                }
            }
            //3.5过滤查询 价格区间过滤
            String price = (String) searchMap.get("price"); // 0-500 3000-*
            if (StringUtils.isNoneBlank(price)) {
                //根据中间的-切割0是最低范围 1是最高范围
                String[] split = price.split("-");
                if ("*".equals(split[1])) {
                    //价格大于  使用范围查询rangeQuery  gte大于等于split截取到的第一个值 也就是3000-*的3000
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(split[0]));
                }else {
                    //100=<price<=500     from 从 100 是否保安true  到500 是否保安 true
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").from(split[0],true).to(split[1],true));
                }
            }


            nativeSearchQueryBuilder.withFilter(boolQueryBuilder);
        } else {
            nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        }
        //4.构建查询对象
        NativeSearchQuery searchQuery = nativeSearchQueryBuilder.build();

        //5.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class, new SearchResultMapper() {

            //自定义 进行结果集的映射   //获取高亮
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse response, Class<T> clazz, Pageable pageable) {
                //1.创建当前的页的集合
                List<T> content = new ArrayList<>();
                //2.获取查询的结果 获取总记录数
                SearchHits hits = response.getHits();
                //3.判断是否有记录 如果没有 返回
                if (hits == null || hits.getTotalHits() <= 0) {
                    return new AggregatedPageImpl(content);
                }
                //4.有记录  获取高亮的数据
                for (SearchHit hit : hits) {
                    Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                    HighlightField highlightField = highlightFields.get("title");//获取高亮的字段的数据对象
                    if (highlightField != null) {
                        StringBuffer sb = new StringBuffer();
                        Text[] fragments = highlightField.getFragments();
                        if (fragments != null && fragments.length > 0) {
                            for (Text fragment : fragments) {
                                String string = fragment.string();//<em style="core">aaaaaaaa</em>
                                sb.append(string);
                            }

                            String sourceAsString = hit.getSourceAsString();//btitem的数据JSON格式
                            TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);
                            tbItem.setTitle(sb.toString());//有高亮
                            content.add((T) tbItem);
                        } else {
                            //设置值。
                        }
                    } else {
                        //没有高亮的数据
                        String sourceAsString = hit.getSourceAsString();//btitem的数据JSON格式
                        TbItem tbItem = JSON.parseObject(sourceAsString, TbItem.class);
                        content.add((T) tbItem);
                    }
                }
                return new AggregatedPageImpl<T>(content, pageable, hits.getTotalHits(), response.getAggregations(), response.getScrollId());
            }
        });


        //6.获取结果集

        //获取分组的结果
        Aggregation category_group = tbItems.getAggregation("category_group");
        StringTerms stringTerms = (StringTerms) category_group;
        System.out.println(stringTerms);
        List<String> categoryList = new ArrayList<>(); //  平板电视  手机
        if (stringTerms != null) {
            List<StringTerms.Bucket> buckets = stringTerms.getBuckets();
            for (StringTerms.Bucket bucket : buckets) {
                categoryList.add(bucket.getKeyAsString());//就是商品分类的名称   平板电视  手机
            }
        }

        //搜索之后 默认 展示第一个商品分类的品牌和规格的列表
        //判断 商品分类是否为空 如果不为空 根据点击到的商品分类查询 该分类下的所有的品牌和规格的列表

        if(StringUtils.isNotBlank(category)) {
            Map map = searchBrandAndSpecList(category);//{ "brandList",[],"specList":[]}
            resultMap.putAll(map);
        }else {
            //否则 查询默认的商品分类下的品牌和规格的列表
            if(categoryList!=null && categoryList.size()>0) {
                Map map = searchBrandAndSpecList(categoryList.get(0));//{ "brandList",[],"specList":[]}
                resultMap.putAll(map);
            }else{
                resultMap.put("specList", new HashMap<>());
                resultMap.put("brandList",new HashMap<>());
            }

        }




        //7.设置结果集到map 返回(总页数 总记录数 当前页的集合 ......)
        resultMap.put("total", tbItems.getTotalElements());
        resultMap.put("rows", tbItems.getContent());//当前页的集合
        resultMap.put("totalPages", tbItems.getTotalPages());//总页数
        resultMap.put("categoryList", categoryList);//商品分类的列表数据


        return resultMap;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     *  根据分类的名称 获取 分类下的品牌的列表 和规格的列表
     * @param category
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        //1.集成redis
        //2.注入rediTemplate
        //3.获取分类的名称对应的模板的ID
        //hset bigkey field1 value1     hget bigkey field1
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);
        //4.根据模板的ID 获取品牌的列表 和规格的列表
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        List<Map>  specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        //5.存储到map中返回
        Map<String,Object> map = new HashMap<>();
        map.put("specList",specList);
        map.put("brandList",brandList);
        return map;

    }

}
