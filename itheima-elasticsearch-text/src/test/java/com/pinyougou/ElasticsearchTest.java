package com.pinyougou;

import com.itheima.es.dao.ItemDao;
import com.itheima.es.model.TbItem;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou *
 * @since 1.0
 */

@ContextConfiguration(locations = "classpath:spring-es.xml")
@RunWith(SpringRunner.class)
public class ElasticsearchTest {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ItemDao dao;

    //创建索引 和映射
    @Test
    public void putMapping(){
        elasticsearchTemplate.createIndex(TbItem.class);
        elasticsearchTemplate.putMapping(TbItem.class);
    }

    //创建文档
    @Test
    public void createDocument(){
        TbItem item = new TbItem();
        item.setId(3000L);
        item.setTitle("华为手机");
        item.setCategory("手机");
        item.setBrand("华为");
        item.setSeller("华为旗舰店");
        Map<String, String> map = new HashMap<>();
        map.put("网络制式","移动4G");
        map.put("机身内存","128G");
        item.setSpecMap(map);//规格的数据 规格的名称(key) 和规格的选项值(value)
        item.setGoodsId(1L);
        dao.save(item);
    }

    //更新
    @Test
    public void updateDocument(){
        TbItem item = new TbItem();
        item.setId(3000L);
        item.setTitle("华为手机");
        item.setCategory("手机");
        item.setBrand("华为");
        item.setSeller("华为旗舰店");
        item.setGoodsId(1L);

        dao.save(item);
    }

    //循环添加数据
    @Test
    public void createAllDocument(){
        for (long i = 0; i < 100; i++) {
            TbItem item = new TbItem();
            item.setId(3000+i);
            item.setTitle("华为手机"+i);
            item.setCategory("手机"+i);
            item.setBrand("华为"+i);
            item.setSeller("华为旗舰店"+i);
            item.setGoodsId(1L);
            dao.save(item);
        }

    }


    //查询所有
    @Test
    public void findAll(){
        Iterable<TbItem> all = dao.findAll();

        for (TbItem item : all) {
            System.out.println(item);
        }
    }

    //根据ID 查询
    @Test
    public void findByID(){
        System.out.println(dao.findById(830972L).get());
    }
    //分页查询
    @Test
    public void findByPAGRE(){

        //第一个参数 指的是 当前的页码 如果是0 标识第一页
        //第二个参数：指定的是 每页显示的行
        Pageable pageable  = PageRequest.of(0,10);
        Page<TbItem> all = dao.findAll(pageable);
        System.out.println("总页数："+all.getTotalPages());
        List<TbItem> content = all.getContent();//当前的页的集合数据
        System.out.println(content);
    }

    //通配符的查询
    // 手机 手表 手套  手? 占用一个字符空间
    //                 手* 不占有字符空间
    @Test
    public void queryBywildcardQuery(){
        //1.创建查询对象
        SearchQuery query = new NativeSearchQuery(QueryBuilders.wildcardQuery("title","华为手机?"));
        //2.设置搜索的查询的条件

        //3.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        //4.获取结果
        List<TbItem> content = tbItems.getContent();//数据集
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数"+totalPages);
        long totalElements = tbItems.getTotalElements();//总记录数
        System.out.println("总记录数"+totalElements);

        System.out.println(content);

    }

    //先分词 再查询
    @Test
    public void mathQuery(){
        //1.创建查询对象
            SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("title","华为手机"));
        //2.设置查询的条件

        //3.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        //4.获取结果
        List<TbItem> content = tbItems.getContent();//数据集
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数"+totalPages);
        long totalElements = tbItems.getTotalElements();//总记录数
        System.out.println("总记录数"+totalElements);

        System.out.println(content);

    }

    //
    @Test
    public void copyToQuery(){
        //1.创建查询对象
        SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("分辨率","手机"));//this.$set(target,key,value)
        //2.设置查询的条件

        //3.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);
        //4.获取结果
        List<TbItem> content = tbItems.getContent();//数据集
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数"+totalPages);
        long totalElements = tbItems.getTotalElements();//总记录数
        System.out.println("总记录数"+totalElements);

        System.out.println(content);

    }

    //查询 机身内存为  128G的手机
    @Test
    public void  queryByOject(){
        //1.创建一个查询的对象

        SearchQuery query = new NativeSearchQuery(QueryBuilders.matchQuery("specMap.机身内存.keyword","128G"));

        //2.设置查询的条件

        //3.执行查询
        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(query, TbItem.class);

        //4.获取结果
        List<TbItem> content = tbItems.getContent();//数据集
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数"+totalPages);
        long totalElements = tbItems.getTotalElements();//总记录数
        System.out.println("总记录数"+totalElements);

        System.out.println(content);


    }

    // 多个条件组合查询
    //  查询 title 为华为 的 数据
    //  查询 网络制式 为 移动4G的数据
    @Test
    public void boolQuery(){
        //1.创建一个查询对象的 构建对象
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();
        //2.设置查询的条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //必须满足 某一个条件  查询 title 为华为 的 数据

        //must  必须要满足  AND
        //MUST_NOT 必须不满足  NOT
        //SHOULD 应该满足   OR
        boolQueryBuilder.filter(QueryBuilders.matchQuery("title","华为"));
        //必须满足 某一个条件  网络制式 为移动4G的数据
        boolQueryBuilder.filter(QueryBuilders.termQuery("specMap.网络制式.keyword","移动4G"));


        builder.withIndices("pinyougou");
        builder.withTypes("item");
        builder.withQuery(boolQueryBuilder);

        //3.构建查询对象
        NativeSearchQuery searchQuery = builder.build();
        //4.执行查询

        AggregatedPage<TbItem> tbItems = elasticsearchTemplate.queryForPage(searchQuery, TbItem.class);
        //5.获取结果
        List<TbItem> content = tbItems.getContent();//数据集
        int totalPages = tbItems.getTotalPages();
        System.out.println("总页数"+totalPages);
        long totalElements = tbItems.getTotalElements();//总记录数
        System.out.println("总记录数"+totalElements);

        System.out.println(content);



    }

}
