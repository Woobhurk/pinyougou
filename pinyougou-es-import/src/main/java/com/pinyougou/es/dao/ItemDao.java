package com.pinyougou.es.dao;

import com.pinyougou.pojo.TbItem;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDao extends ElasticsearchCrudRepository<TbItem,Long> {

}
