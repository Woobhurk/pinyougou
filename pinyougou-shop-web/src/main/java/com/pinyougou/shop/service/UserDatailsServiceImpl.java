package com.pinyougou.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDatailsServiceImpl implements UserDetailsService {


    @Reference
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //从数据库中查询用户的信息
        TbSeller tbSeller = sellerService.findOne(username);
        if(tbSeller==null){
            return null;
        }
        //判断用户的信息的逻辑
        String status = tbSeller.getStatus();

        if(!"1".equals(status)){
            //未审核 就是 账号不可以用
            return null;
        }
        //返回数据给spring security的框架 自动进行匹配

        //需要一个用户名 一个密码 和一个权限集合                                //这个方法是给用户赋予角色    使用逗号分隔 然后把这个字符串转为集合 赋予用户
        return new User(username,   tbSeller.getPassword(), AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_SELLER,ROLE_ADMIN"));
    }
}
