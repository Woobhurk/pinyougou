package com.pinyougou.sellergoods;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import com.pinyougou.common.constant.TimeConfig;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.service.UserService;
import com.pinyougou.sellergoods.voservice.UserActivityStoreService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-trans.xml")
public class UserActivityStoreServiceTest {
    @Autowired
    private UserActivityStoreService userActivityStoreService;
    @Autowired
    private UserService userService;

    @Test
    public void generateUserActivityMap() {
        Calendar calendar = new GregorianCalendar();
        long baseMilliseconds;
        long fakeMilliseconds;
        Date fakeTime;
        List<TbUser> allUserList = this.userService.findAll();
        List<String> allUsernameList = new ArrayList<>();
        Random random = new Random(System.currentTimeMillis());

        calendar.set(2018, Calendar.JANUARY, 1);
        baseMilliseconds = calendar.getTimeInMillis();

        for (TbUser user : allUserList) {
            allUsernameList.add(user.getUsername());
        }

        for (int i = 0; i < 1000; i++) {
            String fakeUsername = allUsernameList.get(random.nextInt(allUsernameList.size()));

            fakeMilliseconds = baseMilliseconds + Math.abs(
                random.nextLong() % (TimeConfig.MSECONDS_PER_YEAR * 3 / 2));
            fakeTime = new Date(fakeMilliseconds);
            this.userActivityStoreService.storeUserActivity(fakeUsername, fakeTime);
        }
    }
}
