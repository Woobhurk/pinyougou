package com.pinyougou.manager.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableWebSecurity //配置注解启动security
public class ManagerSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/css/**", "/img/**", "/js/**", "/plugins/**", "/login.html").permitAll()
                //设置所有的其他请求都需要认证通过即可 也就是用户名和密码正确即可不需要其他的角色就能访问
                .anyRequest().authenticated();
        //配置表单登陆
        http.formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/admin/index.html", true)
                .failureUrl("/login.html?error");

        //退出登陆  注销当前网址
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);
        http.csrf().disable();//关闭csrf

        //开启同源(同属于一个系统下的页面 比如9101下)iframe可以访问策略(如果不开启 在前端页面上 使用iframe引入的其他页面将不会展示)
        http.headers().frameOptions().sameOrigin();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {


        auth.inMemoryAuthentication().
                withUser("admin").
                password("{noop}admin").
                roles("ADMIN");
    }
}
