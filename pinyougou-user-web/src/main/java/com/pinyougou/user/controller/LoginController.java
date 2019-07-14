package com.pinyougou.user.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/login")
public class LoginController {
    @RequestMapping("/name")
    public String getName() {
        System.out.println(11111111);
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
