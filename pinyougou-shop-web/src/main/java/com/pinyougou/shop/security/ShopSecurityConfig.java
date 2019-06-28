package com.pinyougou.shop.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

//安全配置类
@EnableWebSecurity //开启自动配置
public class ShopSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

        //认证
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //从数据库中查询相关的用户信息 不能写死
       // auth.inMemoryAuthentication().withUser("admin").password("{noop}admin").roles("ADMIN");
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);

    }

    //授权
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //放行
        http.authorizeRequests().
                antMatchers(
                        "/css/**",
                        "/img/**",
                        "/js/**",
                        "/plugins/**",
                        "/*.html",
                        "/seller/add.shtml").permitAll()
                //除了上面的公共资源 其他的请求都需要 SELLER 角色
               // .antMatchers("/**").hasRole("SELLER");
                   //设置所有的其他请求都需要认证通过即可 也就是用户名和密码正确即可不需要其他的角色
                   .anyRequest().authenticated();

           //设置自定义登陆页面
        http.formLogin()
                //登陆页
                .loginPage("/shoplogin.html")
                //处理登陆的url
                .loginProcessingUrl("/login")
                //默认成功的网址
                .defaultSuccessUrl("/admin/index.html", true)
                //失败页面
                .failureUrl("/login?error");

        //禁用csrf 如果不禁用 在发送post请求时 如果访问里没有携带 token 会拒绝访问

         http.csrf().disable();
        //http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        //设置iframe 的同源可以访问策略
        http.headers().frameOptions().sameOrigin();
        //注销并销毁session 退出登陆
        http.logout().logoutUrl("/logout").invalidateHttpSession(true);

    }
}
