package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginUserInfoController {

    @RequestMapping("/getName")
    public String getLoginName(){

        //获取用户信息 取得登陆用户的 用户名
       return SecurityContextHolder.getContext().getAuthentication().getName();

    }

}
