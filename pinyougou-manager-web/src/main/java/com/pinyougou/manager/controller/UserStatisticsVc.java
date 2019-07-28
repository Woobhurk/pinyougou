package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.voservice.UserStatisticsService;
import entity.ResultInfo;
import entity.UserStatisticsParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userStatistics")
public class UserStatisticsVc {
    @Reference
    private UserStatisticsService userStatisticsService;

    @RequestMapping("/findUserStatistics")
    public ResultInfo findUserStatistics(@RequestBody UserStatisticsParam userStatisticsParam) {
        ResultInfo resultInfo = new ResultInfo();

        resultInfo.setSuccess(true);
        resultInfo.setData(this.userStatisticsService.findUserStatistics(userStatisticsParam));

        return resultInfo;
    }
}
