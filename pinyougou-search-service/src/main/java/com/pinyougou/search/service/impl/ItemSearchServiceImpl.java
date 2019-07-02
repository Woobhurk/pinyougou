package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchMap) {
        Map<String,Object> resultMap = new HashMap<>();
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        //1.根据关键字搜索
        String keywords = (String) searchMap.get("keywords");
        // 使用多字段查询 multiMatchQuery
        builder.withQuery(QueryBuilders.multiMatchQuery(keywords,"seller","category","brand","title"));
       //2.设置高亮显示的域(字段) 设置前缀 和后缀
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置前缀 使用双引号要加上转义符
        highlightBuilder.preTags("<em style=\"color:red\">").postTags("</em>");

        builder
                //设置高亮字段
                .withHighlightFields(new HighlightBuilder.Field("title"))
                //设置前缀后缀
                .withHighlightBuilder(highlightBuilder);


        //4.构建查询对象
        NativeSearchQuery searchQuery = builder.build();

        //5.执行查询 如果直接执行查询的话是不会有高亮显示
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        //6.获取结果集  返回

        List<TbItem> itemList = tbItems.getContent(); //专门存放 查询到的当前页的集合数据
        long totalElements = tbItems.getTotalElements();//总记录数
        int totalPages = tbItems.getTotalPages();//总页数
        resultMap.put("rows",itemList);
        resultMap.put("total",totalElements);
        resultMap.put("totalPages",totalPages);

        return resultMap;
    }
}
