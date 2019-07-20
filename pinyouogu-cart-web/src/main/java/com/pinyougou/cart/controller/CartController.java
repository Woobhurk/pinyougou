package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;

import com.pinyougou.common.util.CookieUtil;
import entity.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Reference
    private CartService cartService;

    /**
     * 获取购物车的列表
     *
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        //获取用户名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //如果是匿名用户
        if ("anonymousUser".equals(name)) {
            String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if (StringUtils.isEmpty(cartListString)) {
                cartListString = "[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);
            return cookieCartList;
        } else {
            //已经登陆的用户操作redis
            List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
            //登录后要合并 cookie 和 redis 中的购物车数据
            //获取cookie中的数据
            String cartListString = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
            if (StringUtils.isEmpty(cartListString)) {
                cartListString = "[]";
            }
            List<Cart> cookieCartList = JSON.parseArray(cartListString, Cart.class);
            //获取redis中的数据
            if (cartListFromRedis==null) {
                cartListFromRedis=new ArrayList<>();
            }
            //合并 并返回一个最新的购物车
            if (cookieCartList.size()>0) {
                List<Cart> carts = cartService.mergeCartList(cookieCartList, cartListFromRedis);
                cartService.saveCartListToRedis(name,carts );
                //清除cookie中的购物车数据
                CookieUtil.deleteCookie(request, response, "cartList");
                return carts;
            }



            return cartListFromRedis == null ? new ArrayList<>() : cartListFromRedis;
        }

    }


    /**
     * 添加商品到已有的购物车的列表中
     *
     * @param itemId 要添加的商品SKU的ID
     * @param num    购买的数量
     * @return
     */
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");//统一指定的域访问我的服务器资源
            response.setHeader("Access-Control-Allow-Credentials", "true");//同意客户端携带cookie
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(name)) {
                List<Cart> cartList = findCartList(request,response);//获取购物车列表
                cartList = cartService.addGoodsToCartList(cartList, itemId, num);
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 3600 * 24, "UTF-8");
                return new Result(true, "添加成功");
            } else {
                 //获取购物车数据findCartBySellerId
                List<Cart> cartListFromRedis = cartService.findCartListFromRedis(name);
                //向已有的购物车中添加商品
                List<Cart> cartListnew = cartService.addGoodsToCartList(cartListFromRedis, itemId, num);
                //保存最新列表到redis中
                cartService.saveCartListToRedis(name, cartListnew);
                return new Result(true, "保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }


}
