package com.pinyougou.manager.controller;

import java.util.List;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.sellergoods.voservice.UserActivityChartService;
import com.pinyougou.sellergoods.voservice.UserActivityService;
import entity.ResultInfo;
import entity.UserActivityCountResult;
import entity.UserActivityParam;
import entity.UserActivityTimeResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userActivity")
public class UserActivityVc {
    @Reference
    private UserActivityService userActivityService;
    @Reference
    private UserActivityChartService userActivityChartService;

    @RequestMapping("/findUserActivity")
    public ResultInfo userActivity(@RequestBody UserActivityParam userActivityParam) {
        ResultInfo resultInfo = new ResultInfo();

        resultInfo.setSuccess(true);
        resultInfo.setData(this.userActivityService.findUserActivity(userActivityParam));

        return resultInfo;
    }

    @RequestMapping("/findUserActivityTime")
    public ResultInfo userActivityTime(@RequestBody UserActivityParam userActivityParam) {
        ResultInfo resultInfo = new ResultInfo();
        List<UserActivityTimeResult> userActivityTimeResultList;

        userActivityTimeResultList = this.userActivityService.findUserActivityTime(
            userActivityParam);

        resultInfo.setSuccess(true);
        resultInfo.setData(
            this.userActivityChartService.convertToScatterChart(userActivityTimeResultList));

        return resultInfo;
    }

    @RequestMapping("/findUserActivityCount")
    public ResultInfo userActivityCount(@RequestBody UserActivityParam userActivityParam) {
        ResultInfo resultInfo = new ResultInfo();
        UserActivityCountResult userActivityCountResult;

        userActivityCountResult = this.userActivityService.findUserActivityCount(
            userActivityParam);
        resultInfo.setSuccess(true);
        resultInfo.setData(
            this.userActivityChartService.convertToLineChart(userActivityCountResult));

        return resultInfo;
    }
}
