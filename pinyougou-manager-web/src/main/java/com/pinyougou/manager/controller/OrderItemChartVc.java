package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.voservice.OrderItemChartService;
import entity.OrderItemParam;
import entity.ResultInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orderItemChart")
public class OrderItemChartVc {
    @Reference
    private OrderItemChartService orderItemChartService;

    @RequestMapping("/countOrderItem")
    public ResultInfo countOrderItem(@RequestBody OrderItemParam orderItemParam) {
        ResultInfo resultInfo = new ResultInfo();

        resultInfo.setSuccess(true);
        resultInfo.setData(this.orderItemChartService.countOrderItem(orderItemParam));

        return resultInfo;
    }
}
