package com.itheima.security;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity //开启 自动的seuciryt配置
public class MySecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //授权 登陆和错误页面 不需要登陆
        //其他的请求/admin/** 都需要拥有ADMIN的角色才能访问
        //其他的请求/user/** 都需要拥有USER的角色的人 才可以访问
        //其他的任意请求 都只要登陆就可以访问

        http.authorizeRequests().antMatchers("/login.html","/error.html").permitAll() //允许这些页面都能访问
                .antMatchers("/admin/**").hasRole("ADMIN")  //admin/**下的资源只有拥有admin角色的人才能访问
                .antMatchers("/user/**").hasRole("USER")   //user/**下的资源只有拥有user角色的人才能访问
                .anyRequest().authenticated();  //其他的资源请求都需要登陆之后才能访问
        //配置使用表单登陆
        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/KK.jsp", true)
                .failureUrl("/error.html");

        //禁用Csrf  禁用跨站请求伪造
        http.csrf().disable();
    }
}
