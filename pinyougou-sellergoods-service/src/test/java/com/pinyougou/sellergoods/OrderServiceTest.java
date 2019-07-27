package com.pinyougou.sellergoods;

import com.pinyougou.sellergoods.service.OrderService;
import entity.OrderParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-trans.xml")
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @Test
    public void findByPageParam() {
        OrderParam orderParam = new OrderParam();

        orderParam.setStatus("2");
        //orderParam.setTitle("手机");
        System.err.println(this.orderService.findByPageParam(1, 100, orderParam));
    }
}
