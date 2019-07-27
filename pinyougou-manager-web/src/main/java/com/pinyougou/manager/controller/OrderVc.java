package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.service.OrderService;
import entity.OrderParam;
import entity.ResultInfo;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderVc {
    @Reference
    private OrderService orderService;

    @RequestMapping("/findByPageParam/{pageNum}/{pageSize}")
    public ResultInfo findByPageParam(@PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize, @RequestBody OrderParam orderParam) {
        ResultInfo resultInfo = new ResultInfo();

        resultInfo.setSuccess(true);
        resultInfo.setData(this.orderService.findByPageParam(pageNum, pageSize, orderParam));

        return resultInfo;
    }
}
