package com.pinyougou.sellergoods.voservice.impl;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.sellergoods.voservice.UserActivityChartService;
import entity.LineChartData;
import entity.ScatterChartData;
import entity.UserActivityCountResult;
import entity.UserActivityTimeResult;

@Service
@org.springframework.stereotype.Service
public class UserActivityChartServiceImpl implements UserActivityChartService {
    @Override
    public ScatterChartData convertToScatterChart(
        List<UserActivityTimeResult> userActivityTimeResultList) {
        ScatterChartData scatterChartData = new ScatterChartData();

        scatterChartData.setDataList(new ArrayList<>());

        for (UserActivityTimeResult userActivityTimeResult : userActivityTimeResultList) {
            Long[] data = new Long[2];

            data[0] = userActivityTimeResult.getDate();
            data[1] = userActivityTimeResult.getHour();
            scatterChartData.getDataList().add(data);
        }

        return scatterChartData;
    }

    @Override
    public LineChartData<String, Integer> convertToLineChart(
        UserActivityCountResult userActivityCountResult) {
        LineChartData<String, Integer> lineChartData = new LineChartData<>();

        lineChartData.setHorizontalDataList(
            new ArrayList<>(userActivityCountResult.getActivityTime()));
        lineChartData.setVerticalDataList(
            new ArrayList<>(userActivityCountResult.getActivityCount()));

        return lineChartData;
    }
}
