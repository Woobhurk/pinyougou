package com.pinyougou.user.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.constant.SpringSecurityConfig;
import com.pinyougou.user.voservice.UserActivityStoreService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserActivityInterceptor implements HandlerInterceptor {
    @Reference
    private UserActivityStoreService userActivityStoreService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
            && !authentication.getName().equalsIgnoreCase(SpringSecurityConfig.ANONYMOUS_USER)) {
            String username = authentication.getName();

            this.userActivityStoreService.storeUserActivity(username);
        }

        return true;
    }
}
