package com.pinyougou.user.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.constant.SpringSecurityConfig;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserLoginInterceptor implements HandlerInterceptor {
    @Reference
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean handlerLater;

        if (authentication != null
            && authentication.getName().equalsIgnoreCase(SpringSecurityConfig.ANONYMOUS_USER)) {
            // 用户已登录
            String username = authentication.getName();
            TbUser userParam = new TbUser();
            TbUser savedUser;

            userParam.setUsername(username);
            savedUser = this.userService.select(userParam).get(0);

            if (this.handleUser(savedUser)) {
                handlerLater = true;
            } else {
                response.getWriter().println("用户被冻结");
                handlerLater = false;
            }

            System.err.println(authentication.getName());
        } else {
            // 用户未登录
            System.err.println("no username");
            handlerLater = true;
        }

        return handlerLater;
    }

    private boolean handleUser(TbUser user) {
        Date currentTime = new Date();
        boolean handleStatus;

        if (user.getUnfrozen() == null || user.getUnfrozen().before(currentTime)) {
            // 如果用户解冻冻结时间为空或者比现在更早，则设置解冻时间为空，用户登录成功
            user.setUnfrozen(null);
            this.userService.updateByPrimaryKey(user);
            handleStatus = true;
        } else {
            // 用户被冻结，尚未解冻，登录失败
            handleStatus = false;
        }

        return handleStatus;
    }
}
