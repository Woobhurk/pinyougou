package com.pinyougou.sellergoods.voservice;

import java.util.List;
import entity.UserActivityCountResult;
import entity.UserActivityParam;
import entity.UserActivityResult;
import entity.UserActivityTimeResult;

public interface UserActivityService {
    List<UserActivityResult> findUserActivity(UserActivityParam userActivityParam);

    List<UserActivityTimeResult> findUserActivityTime(UserActivityParam userActivityParam);

    UserActivityCountResult findUserActivityCount(UserActivityParam userActivityParam);
}
