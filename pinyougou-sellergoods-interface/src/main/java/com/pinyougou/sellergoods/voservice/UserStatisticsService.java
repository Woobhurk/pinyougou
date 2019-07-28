package com.pinyougou.sellergoods.voservice;

import entity.UserStatisticsParam;
import entity.UserStatisticsResult;

public interface UserStatisticsService {
    UserStatisticsResult findUserStatistics(UserStatisticsParam userStatisticsParam);
}
