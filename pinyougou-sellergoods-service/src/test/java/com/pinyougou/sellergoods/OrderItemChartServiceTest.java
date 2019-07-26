package com.pinyougou.sellergoods;

import com.alibaba.fastjson.JSON;
import com.pinyougou.sellergoods.voservice.OrderItemChartService;
import entity.OrderItemChartParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-trans.xml")
public class OrderItemChartServiceTest {
    @Autowired
    private OrderItemChartService orderItemChartService;

    @Test
    public void retrieveOrderChart() {
        OrderItemChartParam orderItemChartParam = new OrderItemChartParam();

        orderItemChartParam.setParentId(0L);
        System.err.println(JSON.toJSON(this.orderItemChartService.countOrderItem(
            orderItemChartParam)));
    }
}
