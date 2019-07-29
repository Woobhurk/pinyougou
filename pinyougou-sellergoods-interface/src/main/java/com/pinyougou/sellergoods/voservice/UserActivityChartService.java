package com.pinyougou.sellergoods.voservice;

import java.util.List;
import entity.LineChartData;
import entity.ScatterChartData;
import entity.UserActivityCountResult;
import entity.UserActivityTimeResult;

public interface UserActivityChartService {
    ScatterChartData convertToScatterChart(List<UserActivityTimeResult> userActivityTimeResultList);

    LineChartData<String, Integer> convertToLineChart(
        UserActivityCountResult userActivityCountResult);
}
