package com.pinyougou.cart.service;

import entity.Cart;

import java.util.List;

public interface CartService {

    /**
     * 向已有的购物车添加商品
     * @param cartList 已有的购物车
     * @param itemId 商品的ID
     * @param num 要购买的数量
     * @return
     */
   List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    /**
     * 从redis中查询购物车
     * @param username
     * @return
     */
     List<Cart> findCartListFromRedis(String username);

    /**
     * 将购物车保存到redis
     * @param username
     * @param cartList
     */
     void saveCartListToRedis(String username,List<Cart> cartList);


     List<Cart> mergeCartList(List<Cart> cookieList,List<Cart> redisList);
}
