package com.pinyougou.user.voservice;

import java.util.Date;

public interface UserActivityStoreService {
    void storeUserActivity(String username);

    void storeUserActivity(String username, Date currentTime);
}
