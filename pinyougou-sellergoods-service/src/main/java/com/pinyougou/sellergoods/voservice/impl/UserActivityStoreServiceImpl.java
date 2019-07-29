package com.pinyougou.sellergoods.voservice.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.constant.RedisConfig;
import com.pinyougou.common.constant.TimeConfig;
import com.pinyougou.sellergoods.voservice.UserActivityStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

@Service
@org.springframework.stereotype.Service
public class UserActivityStoreServiceImpl implements UserActivityStoreService {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    //private Logger logger = SimpleLogger.getLogger(this.getClass());

    @Override
    public void storeUserActivity(String username) {
        this.storeUserActivity(username, new Date());
    }

    @Override
    public void storeUserActivity(String username, Date currentTime) {
        BoundHashOperations<Object, Object, Object> userActivityOperation = this.redisTemplate
            .boundHashOps(RedisConfig.KEY_USER_ACTIVITY);
        Map<String, Integer> savedUserActivityMap;
        Map<String, Integer> userActivityMap;
        Long hourMilliseconds = this.retrieveHourMilliseconds(currentTime);

        savedUserActivityMap = (Map<String, Integer>) userActivityOperation.get(hourMilliseconds);

        if (savedUserActivityMap == null) {
            userActivityMap = new HashMap<>();
        } else {
            userActivityMap = savedUserActivityMap;
        }

        if (userActivityMap.containsKey(username)) {
            userActivityMap.put(username, userActivityMap.get(username) + 1);
        } else {
            userActivityMap.put(username, 1);
        }

        //this.logger.info("userActivityMap " + userActivityMap);
        userActivityOperation.put(hourMilliseconds, userActivityMap);
    }

    /*******************************************************/
    private Long retrieveHourMilliseconds(Date currentTime) {
        return currentTime.getTime()
            / TimeConfig.MSECONDS_PER_HOUR
            * TimeConfig.MSECONDS_PER_HOUR;
    }
}
