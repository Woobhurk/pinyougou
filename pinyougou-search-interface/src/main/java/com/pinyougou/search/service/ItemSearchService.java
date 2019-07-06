package com.pinyougou.search.service;

import com.pinyougou.pojo.TbItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ItemSearchService {

    /**
     * 根据搜索条件搜索内容展示数据返回
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String, Object> searchMap);

    /**
     * 更新ES索引
     * @param tbItemList 要更新的数据
     */
    void updateIndex(List<TbItem> tbItemList);

    /**
     * 移除ES中的数据
     * @param ids
     */
    void deleteByIds(Long[] ids);
}
