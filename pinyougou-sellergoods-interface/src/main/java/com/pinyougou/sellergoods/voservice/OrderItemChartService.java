package com.pinyougou.sellergoods.voservice;

import java.util.List;
import entity.OrderItemChart;
import entity.OrderItemParam;

public interface OrderItemChartService {
    List<OrderItemChart> countOrderItem(OrderItemParam orderItemParam);
}
