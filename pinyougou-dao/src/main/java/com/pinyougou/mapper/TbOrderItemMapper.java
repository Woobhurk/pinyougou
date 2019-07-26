package com.pinyougou.mapper;

import com.pinyougou.pojo.TbOrderItem;
import entity.OrderItemChartParam;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TbOrderItemMapper extends Mapper<TbOrderItem> {
    Double countPriceOfCategory(@Param("orderItemParam") OrderItemChartParam orderItemChartParam);
}
