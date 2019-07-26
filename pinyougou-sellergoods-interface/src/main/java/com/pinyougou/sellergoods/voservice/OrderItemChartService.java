package com.pinyougou.sellergoods.voservice;

import java.util.List;
import entity.OrderItemChart;
import entity.OrderItemChartParam;

public interface OrderItemChartService {
    List<OrderItemChart> countOrderItem(OrderItemChartParam orderItemChartParam);
}
