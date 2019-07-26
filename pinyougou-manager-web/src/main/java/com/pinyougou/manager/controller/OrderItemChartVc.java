package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.voservice.OrderItemChartService;
import entity.OrderItemChartParam;
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
    public ResultInfo countOrderItem(@RequestBody OrderItemChartParam orderItemChartParam) {
        ResultInfo resultInfo = new ResultInfo();

        resultInfo.setSuccess(true);
        resultInfo.setData(this.orderItemChartService.countOrderItem(orderItemChartParam));

        return resultInfo;
    }
}
