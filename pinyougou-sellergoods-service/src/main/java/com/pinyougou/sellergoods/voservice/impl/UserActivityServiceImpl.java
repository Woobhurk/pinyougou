package com.pinyougou.sellergoods.voservice.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.constant.RedisConfig;
import com.pinyougou.common.constant.TimeConfig;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.voservice.UserActivityService;
import entity.UserActivityCountResult;
import entity.UserActivityParam;
import entity.UserActivityResult;
import entity.UserActivityTimeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Service
@org.springframework.stereotype.Service
public class UserActivityServiceImpl implements UserActivityService {
    @Autowired
    private TbUserMapper userMapper;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public List<UserActivityResult> findUserActivity(UserActivityParam userActivityParam) {
        Map<Long, Map<String, Integer>> allUserActivityMap;
        Map<Long, Map<String, Integer>> filteredAllUserActivityMap;
        Map<Long, Map<String, Integer>> convertedAllUserActivityMap;
        List<UserActivityResult> userActivityResultList;

        allUserActivityMap = this.buildAllUserActivityMap();
        filteredAllUserActivityMap = this
            .filterAllUserActivityMap(allUserActivityMap, userActivityParam);
        convertedAllUserActivityMap = this.convertAllUserActivityMapUnit(
            filteredAllUserActivityMap, userActivityParam);
        userActivityResultList = this.analyseUserActivity(convertedAllUserActivityMap);

        return userActivityResultList;
    }

    @Override
    public List<UserActivityTimeResult> findUserActivityTime(UserActivityParam userActivityParam) {
        Map<Long, Map<String, Integer>> allUserActivityMap;
        Map<Long, Map<String, Integer>> filteredAllUserActivityMap;
        //Map<Long, Map<String, Integer>> convertedAllUserActivityMap;
        List<UserActivityTimeResult> userActivityTimeResultList;

        allUserActivityMap = this.buildAllUserActivityMap();
        filteredAllUserActivityMap = this
            .filterAllUserActivityMap(allUserActivityMap, userActivityParam);
        //convertedAllUserActivityMap = this.convertAllUserActivityMapUnit(
        //    filteredAllUserActivityMap, userActivityParam);
        userActivityTimeResultList = this.analyseUserActivityTime(
            filteredAllUserActivityMap, userActivityParam);

        return userActivityTimeResultList;
    }

    @Override
    public UserActivityCountResult findUserActivityCount(
        UserActivityParam userActivityParam) {
        Map<Long, Map<String, Integer>> allUserActivityMap;
        Map<Long, Map<String, Integer>> filteredAllUserActivityMap;
        Map<Long, Map<String, Integer>> convertedAllUserActivityMap;
        UserActivityCountResult userActivityCountResult;

        allUserActivityMap = this.buildAllUserActivityMap();
        filteredAllUserActivityMap = this
            .filterAllUserActivityMap(allUserActivityMap, userActivityParam);
        convertedAllUserActivityMap = this.convertAllUserActivityMapUnit(
            filteredAllUserActivityMap, userActivityParam);
        userActivityCountResult = this.analyseUserActivityCount(
            convertedAllUserActivityMap, userActivityParam);

        return userActivityCountResult;
    }

    /***************************************************/
    private List<UserActivityResult> analyseUserActivity(
        Map<Long, Map<String, Integer>> allUserActivityMap) {
        Map<String, Integer> mergedUserActivityMap = new HashMap<>();
        List<UserActivityResult> userActivityResultList = new ArrayList<>();
        TbUser userParam = new TbUser();

        for (Map.Entry<Long, Map<String, Integer>> allUserActivityEntry
            : allUserActivityMap.entrySet()) {
            Map<String, Integer> perUserActivityMap = allUserActivityEntry.getValue();

            for (Map.Entry<String, Integer> perUserActivityEntry
                : perUserActivityMap.entrySet()) {
                String key = perUserActivityEntry.getKey();

                if (mergedUserActivityMap.containsKey(key)) {
                    mergedUserActivityMap.replace(key,
                        mergedUserActivityMap.get(key) + perUserActivityEntry.getValue());
                } else {
                    mergedUserActivityMap.put(key, perUserActivityEntry.getValue());
                }
            }
        }

        for (Map.Entry<String, Integer> entry : mergedUserActivityMap.entrySet()) {
            UserActivityResult userActivityResult = new UserActivityResult();
            String username = entry.getKey();
            TbUser savedUser;

            userParam.setUsername(username);
            savedUser = this.userMapper.select(userParam).get(0);
            userActivityResult.setUsername(username);
            userActivityResult.setPhone(savedUser.getPhone());
            userActivityResult.setLastLoginTime(savedUser.getLastLoginTime());
            userActivityResult.setActivityRate(entry.getValue());
            userActivityResultList.add(userActivityResult);
        }

        userActivityResultList.sort((o1, o2) -> o2.getActivityRate() - o1.getActivityRate());

        return userActivityResultList;
    }

    private List<UserActivityTimeResult> analyseUserActivityTime(
        Map<Long, Map<String, Integer>> allUserActivityMap, UserActivityParam userActivityParam) {
        List<UserActivityTimeResult> userActivityTimeResultList = new ArrayList<>();
        long timeUnitValue = TimeConfig.VALUES[userActivityParam.getTimeUnit()];

        for (Map.Entry<Long, Map<String, Integer>> allUserActivityEntry
            : allUserActivityMap.entrySet()) {
            Map<String, Integer> perUserActivityMap = allUserActivityEntry.getValue();
            long dateMilliseconds = allUserActivityEntry.getKey() / timeUnitValue * timeUnitValue;
            long dayMilliseconds = allUserActivityEntry.getKey() % TimeConfig.MSECONDS_PER_DAY;

            for (Map.Entry<String, Integer> entry : perUserActivityMap.entrySet()) {
                UserActivityTimeResult userActivityTimeResult = new UserActivityTimeResult();

                userActivityTimeResult.setDate(dateMilliseconds);
                userActivityTimeResult.setHour(dayMilliseconds);
                userActivityTimeResultList.add(userActivityTimeResult);
            }
        }

        return userActivityTimeResultList;
    }

    private UserActivityCountResult analyseUserActivityCount(
        Map<Long, Map<String, Integer>> allUserActivityMap, UserActivityParam userActivityParam) {
        Map<String, Integer> userActivityCountMap = new HashMap<>();
        UserActivityCountResult userActivityCountResult = new UserActivityCountResult();
        SimpleDateFormat sdf = new SimpleDateFormat(
            TimeConfig.FORMATS[userActivityParam.getTimeUnit()]);
        long timeUnitValue = TimeConfig.VALUES[userActivityParam.getTimeUnit()];
        long startTimeMilliseconds = userActivityParam.getStartTime().getTime()
            / timeUnitValue * timeUnitValue;
        long endTimeMilliseconds = userActivityParam.getEndTime().getTime()
            / timeUnitValue * timeUnitValue + timeUnitValue;

        for (long currentMilliseconds = startTimeMilliseconds;
             currentMilliseconds < endTimeMilliseconds;
             currentMilliseconds += timeUnitValue) {
            String timeStr = sdf.format(new Date(currentMilliseconds));

            userActivityCountMap.put(timeStr, 0);
        }

        for (Map.Entry<Long, Map<String, Integer>> allUserActivityEntry
            : allUserActivityMap.entrySet()) {
            Map<String, Integer> perUserActivityMap = allUserActivityEntry.getValue();
            String timeStr = sdf.format(new Date(allUserActivityEntry.getKey()));
            int userCount = 0;

            for (Map.Entry<String, Integer> entry : perUserActivityMap.entrySet()) {
                userCount += entry.getValue();
            }

            if (userActivityCountMap.containsKey(timeStr)) {
                userActivityCountMap.replace(timeStr, userActivityCountMap.get(timeStr) + userCount);
            } else {
                userActivityCountMap.put(timeStr, userCount);
            }
        }

        List<String> timeStrList = new ArrayList<>(userActivityCountMap.keySet());

        timeStrList.sort(String::compareTo);
        userActivityCountResult.setActivityTime(timeStrList);
        userActivityCountResult.setActivityCount(new ArrayList<>());

        for (String timeStr : timeStrList) {
            userActivityCountResult.getActivityCount().add(userActivityCountMap.get(timeStr));
        }

        return userActivityCountResult;
    }

    private Map<Long, Map<String, Integer>> buildAllUserActivityMap() {
        BoundHashOperations<Object, Object, Object> allUserActivityOperation =
            this.redisTemplate.boundHashOps(RedisConfig.KEY_USER_ACTIVITY);
        Map<Long, Map<String, Integer>> allUserActivityMap = new HashMap<>();
        Map<Object, Object> rawMap = allUserActivityOperation.entries();

        if (rawMap != null) {
            for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
                Long hourMilliseconds = (Long) entry.getKey();
                Map<String, Integer> hourUserActivityMap =
                    (Map<String, Integer>) entry.getValue();

                allUserActivityMap.put(hourMilliseconds, hourUserActivityMap);
            }
        }

        return allUserActivityMap;
    }

    private Map<Long, Map<String, Integer>> filterAllUserActivityMap(
        Map<Long, Map<String, Integer>> allUserActivityMap, UserActivityParam userActivityParam) {
        Map<Long, Map<String, Integer>> filteredAllUserActivityMap;
        long timeUnitValue = TimeConfig.VALUES[userActivityParam.getTimeUnit()];
        long startTimeMilliseconds = userActivityParam.getStartTime().getTime()
            / timeUnitValue * timeUnitValue;
        long endTimeMilliseconds = userActivityParam.getEndTime().getTime()
            / timeUnitValue * timeUnitValue + timeUnitValue;

        filteredAllUserActivityMap = allUserActivityMap.entrySet().stream()
            .filter(entry ->
                entry.getKey() >= startTimeMilliseconds && entry.getKey() <= endTimeMilliseconds)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            //.forEach(entry ->
            //    filteredAllUserActivityMap.put(entry.getKey(), entry.getValue()));

        return filteredAllUserActivityMap;
    }

    private Map<Long, Map<String, Integer>> convertAllUserActivityMapUnit(
        Map<Long, Map<String, Integer>> allUserActivityMap, UserActivityParam userActivityParam) {
        Map<Long, Map<String, Integer>> convertedAllUserActivityMap = new HashMap<>();
        Long timeUnitValues = TimeConfig.VALUES[userActivityParam.getTimeUnit()];

        for (Map.Entry<Long, Map<String, Integer>> entry : allUserActivityMap.entrySet()) {
            Long timeMilliseconds = entry.getKey() / timeUnitValues * timeUnitValues;

            if (convertedAllUserActivityMap.containsKey(timeMilliseconds)) {
                convertedAllUserActivityMap.replace(timeMilliseconds, this.mergeMapValue(
                    convertedAllUserActivityMap.get(timeMilliseconds),
                    entry.getValue()));
            } else {
                convertedAllUserActivityMap.put(timeMilliseconds, entry.getValue());
            }
        }

        return convertedAllUserActivityMap;
    }

    private Map<String, Integer> mergeMapValue(Map<String, Integer> mapA,
        Map<String, Integer> mapB) {
        // 将mapA的全部数据给合并后的map
        Map<String, Integer> mergedMap = mapA;

        for (Map.Entry<String, Integer> entryA : mapA.entrySet()) {
            // 合并两个map共有的数据，即数值相加
            String key = entryA.getKey();

            // 跳过mapB没有的值
            if (mapB.containsKey(key)) {
                mergedMap.put(key, entryA.getValue() + mapB.get(key));
            }
        }

        for (Map.Entry<String, Integer> entryB : mapB.entrySet()) {
            // 将mapB特有的值给合并后的map
            String key = entryB.getKey();

            // 跳过已经合并了的值
            if (!mergedMap.containsKey(key)) {
                mergedMap.put(entryB.getKey(), entryB.getValue());
            }
        }

        return mergedMap;
    }
}
