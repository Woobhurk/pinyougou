package com.pinyougou.mapper;

import java.util.List;
import com.pinyougou.pojo.TbOrderItem;
import entity.OrderItemChartParam;
import entity.OrderParam;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface TbOrderItemMapper extends Mapper<TbOrderItem> {
    Double countPriceOfCategory(@Param("orderItemChartParam") OrderItemChartParam orderItemChartParam);

    List<TbOrderItem> findByParam(@Param("orderParam") OrderParam orderParam);
}
