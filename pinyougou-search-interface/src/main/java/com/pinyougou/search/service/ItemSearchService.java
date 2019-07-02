package com.pinyougou.search.service;

import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ItemSearchService {

    /**
     * 根据搜索条件搜索内容展示数据返回
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map<String, Object> searchMap);

}
