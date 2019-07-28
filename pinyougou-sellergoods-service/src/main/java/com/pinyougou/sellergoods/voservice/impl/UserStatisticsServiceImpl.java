package com.pinyougou.sellergoods.voservice.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.constant.UserStatisticsConfig;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.voservice.UserStatisticsService;
import entity.UserStatisticsParam;
import entity.UserStatisticsResult;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserStatisticsServiceImpl implements UserStatisticsService {
    @Autowired
    private TbUserMapper userMapper;

    @Override
    public UserStatisticsResult findUserStatistics(UserStatisticsParam userStatisticsParam) {
        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
        List<TbUser> savedUserList;
        Map<String, Integer> userStatisticsMap;
        UserStatisticsResult userStatisticsResult;
        Date startTime;
        Date endTime;
        Integer timeDelta;

        if (userStatisticsParam.getStartTime() == null) {
            startTime = new Date(0);
        } else {
            startTime = userStatisticsParam.getStartTime();
        }

        if (userStatisticsParam.getEndTime() == null) {
            endTime = new Date();
        } else {
            endTime = userStatisticsParam.getEndTime();
        }

        if (userStatisticsParam.getTimeDelta() < UserStatisticsConfig.TIME_DELTA_HOUR
            || userStatisticsParam.getTimeDelta() > UserStatisticsConfig.TIME_DELTA_MONTH) {
            timeDelta = UserStatisticsConfig.TIME_DELTA_HOUR;
        } else {
            timeDelta = userStatisticsParam.getTimeDelta();
        }

        criteria.andGreaterThanOrEqualTo("created", startTime);
        criteria.andLessThanOrEqualTo("created", endTime);
        savedUserList = this.userMapper.selectByExample(example);

        userStatisticsMap = this.buildUserStatisticsMap(savedUserList, timeDelta);
        userStatisticsResult = this.saveUserStatisticsResult(userStatisticsMap);

        return userStatisticsResult;
    }

    /*****************************************************************************/
    private Map<String, Integer> buildUserStatisticsMap(List<TbUser> savedUserList,
        Integer timeDelta) {
        Map<String, Integer> userStatisticsMap = new HashMap<>();
        TbUser[] extremeCreatedUser = this.findExtremeCreatedUser(savedUserList);
        long earliestMilliseconds = this.findExtremeCreatedTime(extremeCreatedUser, timeDelta)[0];
        long latestMilliseconds = this.findExtremeCreatedTime(extremeCreatedUser, timeDelta)[1];
        SimpleDateFormat sdf;

        sdf = new SimpleDateFormat(UserStatisticsConfig.TIME_FORMATS[timeDelta]);

        // 此方法是用户优先，只存储用户注册的时间段数据
        //for (TbUser user : savedUserList) {
        //    long createdMilliseconds = user.getCreated().getTime()
        //        / UserStatisticsConfig.TIME_DELTAS[timeDelta]
        //        * UserStatisticsConfig.TIME_DELTAS[timeDelta];
        //    String createdTimeStr = sdf.format(new Date(createdMilliseconds));
        //
        //    if (userStatisticsMap.containsKey(createdTimeStr)) {
        //        userStatisticsMap.put(createdTimeStr, userStatisticsMap.get(createdTimeStr) + 1);
        //    } else {
        //        userStatisticsMap.put(createdTimeStr, 1);
        //    }
        //}

        // 此方法是时间段优先，不管某时间段是否有用户注册都会存储数据
        for (long current = earliestMilliseconds; current < latestMilliseconds;
             current += UserStatisticsConfig.TIME_DELTAS[timeDelta]) {
            String createdTimeStr = sdf.format(new Date(current));

            userStatisticsMap.put(createdTimeStr, 0);
        }

        for (TbUser user : savedUserList) {
            long createdMilliseconds = user.getCreated().getTime()
                / UserStatisticsConfig.TIME_DELTAS[timeDelta]
                * UserStatisticsConfig.TIME_DELTAS[timeDelta];
            String createdTimeStr = sdf.format(new Date(createdMilliseconds));

            userStatisticsMap.put(createdTimeStr, userStatisticsMap.get(createdTimeStr) + 1);
        }

        return userStatisticsMap;
    }

    private UserStatisticsResult saveUserStatisticsResult(Map<String, Integer> userStatisticsMap) {
        UserStatisticsResult userStatisticsResult = new UserStatisticsResult();
        List<String> timeStrList = new ArrayList<>(userStatisticsMap.keySet());

        timeStrList.sort(String::compareTo);
        userStatisticsResult.setHorizontalDataList(new ArrayList<>());
        userStatisticsResult.setVerticalDataList(new ArrayList<>());

        for (String timeStr : timeStrList) {
            userStatisticsResult.getHorizontalDataList().add(timeStr);
            userStatisticsResult.getVerticalDataList().add(userStatisticsMap.get(timeStr));
        }

        //for (Map.Entry<String, Integer> entry : userStatisticsMap.entrySet()) {
        //    userStatisticsResult.getHorizontalDataList().add(entry.getKey());
        //    userStatisticsResult.getVerticalDataList().add(entry.getValue());
        //}

        return userStatisticsResult;
    }

    private TbUser[] findExtremeCreatedUser(List<TbUser> userList) {
        TbUser earliestUser = userList.get(0);
        TbUser latestUser = userList.get(0);

        for (int i = 1; i < userList.size(); i++) {
            TbUser currentUser = userList.get(i);

            if (earliestUser.getCreated().after(currentUser.getCreated())) {
                earliestUser = currentUser;
            }

            if (latestUser.getCreated().before(currentUser.getCreated())) {
                latestUser = currentUser;
            }
        }

        return new TbUser[]{earliestUser, latestUser};
    }

    private long[] findExtremeCreatedTime(TbUser[] extremeCreatedUser, int timeDelta) {
        long earliestMilliseconds = extremeCreatedUser[0].getCreated().getTime()
            / UserStatisticsConfig.TIME_DELTAS[timeDelta]
            * UserStatisticsConfig.TIME_DELTAS[timeDelta];
        long latestMilliseconds = extremeCreatedUser[1].getCreated().getTime()
            / UserStatisticsConfig.TIME_DELTAS[timeDelta]
            * UserStatisticsConfig.TIME_DELTAS[timeDelta]
            + UserStatisticsConfig.TIME_DELTAS[timeDelta];

        return new long[]{earliestMilliseconds, latestMilliseconds};
    }
}