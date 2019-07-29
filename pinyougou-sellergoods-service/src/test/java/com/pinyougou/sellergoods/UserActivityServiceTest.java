package com.pinyougou.sellergoods;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Logger;
import com.pinyougou.common.constant.TimeConfig;
import com.pinyougou.common.util.JsonUtils;
import com.pinyougou.common.util.SimpleLogger;
import com.pinyougou.sellergoods.voservice.UserActivityService;
import entity.UserActivityCountResult;
import entity.UserActivityParam;
import entity.UserActivityResult;
import entity.UserActivityTimeResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-trans.xml")
public class UserActivityServiceTest {
    @Autowired
    private UserActivityService userActivityService;
    private Logger logger = SimpleLogger.getLogger(UserActivityServiceTest.class);

    @Test
    public void buildUserActivityResult() {
        UserActivityParam userActivityParam = new UserActivityParam();
        Calendar calendar = new GregorianCalendar();

        calendar.set(2018, Calendar.JANUARY, 1, 0, 0);
        userActivityParam.setStartTime(new Date(calendar.getTimeInMillis()));
        calendar.set(2019, Calendar.JANUARY, 1, 0, 0);
        userActivityParam.setEndTime(new Date(calendar.getTimeInMillis()));
        userActivityParam.setTimeUnit(TimeConfig.UNIT_MONTH);
        List<UserActivityResult> userActivityResultList = this.userActivityService
            .findUserActivity(userActivityParam);

        try {
            this.logger.info(JsonUtils.toJson(userActivityResultList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void buildUserActivityTimeResult() {
        UserActivityParam userActivityParam = new UserActivityParam();
        Calendar calendar = new GregorianCalendar();

        calendar.set(2018, Calendar.JANUARY, 1, 0, 0);
        userActivityParam.setStartTime(new Date(calendar.getTimeInMillis()));
        calendar.set(2019, Calendar.JANUARY, 1, 0, 0);
        userActivityParam.setEndTime(new Date(calendar.getTimeInMillis()));
        userActivityParam.setTimeUnit(TimeConfig.UNIT_MONTH);
        List<UserActivityTimeResult> userActivityTimeResultList = this.userActivityService
            .findUserActivityTime(userActivityParam);

        try {
            this.logger.info(JsonUtils.toJson(userActivityTimeResultList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void buildUserActivityCountResult() {
        UserActivityParam userActivityParam = new UserActivityParam();
        Calendar calendar = new GregorianCalendar();

        calendar.set(2018, Calendar.JANUARY, 1, 0, 0);
        userActivityParam.setStartTime(new Date(calendar.getTimeInMillis()));
        calendar.set(2019, Calendar.JANUARY, 1, 0, 0);
        userActivityParam.setEndTime(new Date(calendar.getTimeInMillis()));
        userActivityParam.setTimeUnit(TimeConfig.UNIT_MONTH);
        UserActivityCountResult userActivityCountResult = this.userActivityService
            .findUserActivityCount(userActivityParam);

        try {
            this.logger.info(JsonUtils.toJson(userActivityCountResult));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
