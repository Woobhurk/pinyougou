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
            ||userStatisticsParam.getTimeDelta() > UserStatisticsConfig.TIME_DELTA_MONTH) {
            timeDelta = UserStatisticsConfig.TIME_DELTA_HOUR;
        } else {
            timeDelta = userStatisticsParam.getTimeDelta();
        }

        criteria.andGreaterThanOrEqualTo("created", startTime);
        criteria.andLessThanOrEqualTo("created", endTime);
        savedUserList = this.userMapper.selectByExample(example);

        userStatisticsMap = this.buildUserStatisticsMap(timeDelta, savedUserList);
        System.err.println("================ " + userStatisticsMap);
        userStatisticsResult = this.saveUserStatisticsResult(userStatisticsMap);

        return userStatisticsResult;
    }

    /********************************************************************/
    private Map<String, Integer> buildUserStatisticsMap(Integer timeDelta,
        List<TbUser> savedUserList) {
        Map<String, Integer> userStatisticsMap = new HashMap<>();
        SimpleDateFormat sdf;

        sdf = new SimpleDateFormat(UserStatisticsConfig.TIME_FORMATS[timeDelta]);

        for (TbUser user : savedUserList) {
            long createdMilliseconds = user.getCreated().getTime()
                / UserStatisticsConfig.TIME_DELTAS[timeDelta]
                * UserStatisticsConfig.TIME_DELTAS[timeDelta];
            String createdTimeStr = sdf.format(new Date(createdMilliseconds));

            if (userStatisticsMap.containsKey(createdTimeStr)) {
                userStatisticsMap.put(createdTimeStr, userStatisticsMap.get(createdTimeStr) + 1);
            } else {
                userStatisticsMap.put(createdTimeStr, 1);
            }
        }

        return userStatisticsMap;
    }

    private UserStatisticsResult saveUserStatisticsResult(Map<String, Integer> userStatisticsMap) {
        UserStatisticsResult userStatisticsResult = new UserStatisticsResult();

        userStatisticsResult.setHorizontalDataList(new ArrayList<>());
        userStatisticsResult.setVerticalDataList(new ArrayList<>());

        for (Map.Entry<String, Integer> entry : userStatisticsMap.entrySet()) {
            userStatisticsResult.getHorizontalDataList().add(entry.getKey());
            userStatisticsResult.getVerticalDataList().add(entry.getValue());
        }

        return userStatisticsResult;
    }
}
