package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.constant.Constant;
import com.pinyougou.mapper.TbItemMapper;
import entity.Cart;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;



    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.根据商品SKU ID查询SKU商品信息
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //2.获取商家ID
        String sellerId = tbItem.getSellerId();
        //3.根据商家ID判断购物车列表中是否存在该商家的购物车
        Cart cart = findCartBySellerId(sellerId, cartList);
        if (cart == null) {
            //4.如果购物车列表中不存在该商家的购物车
            //4.1 新建购物车对象
            cart = new Cart();
            //设置店铺id 和店铺名
            cart.setSellerId(sellerId);
            cart.setSellerName(tbItem.getSeller());
            //购物车明细列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            //购物车里的单个商品
            TbOrderItem orderItemNew = new TbOrderItem();
            //补充商品属性
            orderItemNew.setItemId(itemId);
            orderItemNew.setGoodsId(tbItem.getGoodsId());
            orderItemNew.setTitle(tbItem.getTitle());
            orderItemNew.setPrice(tbItem.getPrice());
            orderItemNew.setNum(num);//传递过来的购买的数量
            //转换为double计算总价
            double v = num * tbItem.getPrice().doubleValue();
            //转回来设置
            orderItemNew.setTotalFee(new BigDecimal(v));//金额
            orderItemNew.setPicPath(tbItem.getImage());//商品的图片路径

            orderItemList.add(orderItemNew);
            //4.2 将新建的购物车对象添加到购物车列表
            cart.setOrderItemList(orderItemList);
            cartList.add(cart);

        } else {
            //5.如果购物车列表中存在该商家的购物车
            // 查询购物车明细列表中是否存在该商品
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            TbOrderItem orderItem = findOrderItemByItemId(itemId, orderItemList);
            //判断商家购物车有没有该商品
            if (orderItem!=null) {
                //5.1.  有，在原购物车明细上添加数量，更改金额
                orderItem.setNum(orderItem.getNum()+num);
                //金额重新计算 数量+单价
                double v = orderItem.getNum() * orderItem.getPrice().doubleValue();
                //设置添加后的价格
                orderItem.setTotalFee(new BigDecimal(v));
                //判断如果商品的购买数量为0 表示不买 需要删除
                if (orderItem.getNum()==0) {
                    orderItemList.remove(orderItem);
                }
                //如果整个购物车为空 表示用户没有购买该商家的商品 直接删除 商家购物车
                if (orderItemList.size()==0) {
                    cartList.remove(cart);
                }
            }else {
                // 要添加的商品 如果 没有存在商家的明细列表中  说明 没有商品 。   直接添加该商品到明细中
                TbOrderItem orderItemNew = new TbOrderItem();
                //设置他的属性
                orderItemNew.setItemId(itemId);
                orderItemNew.setGoodsId(tbItem.getGoodsId());
                orderItemNew.setTitle(tbItem.getTitle());
                orderItemNew.setPrice(tbItem.getPrice());
                orderItemNew.setNum(num);//传递过来的购买的数量
                double v = num * tbItem.getPrice().doubleValue();
                orderItemNew.setTotalFee(new BigDecimal(v));//金额
                orderItemNew.setPicPath(tbItem.getImage());//商品的图片路径
                orderItemList.add(orderItemNew);
            }
        }

        return cartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {
        return (List<Cart>) redisTemplate.boundHashOps(Constant.CART_REDIS_KEY).get(username);
    }

    @Override
    public void saveCartListToRedis(String username, List<Cart> cartList) {
        redisTemplate.boundHashOps(Constant.CART_REDIS_KEY).put(username,cartList);
    }

    /**
     *
     * @param itemId
     * @param orderItemList
     * @return
     */
    private TbOrderItem findOrderItemByItemId(Long itemId, List<TbOrderItem> orderItemList) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue()==itemId) {
                return orderItem;
            }
        }
        return null;
    }

    /**
     * 判断购物车中是否包含选中商品的id
     *
     * @param sellerId
     * @param cartList
     * @return
     */
    private Cart findCartBySellerId(String sellerId, List<Cart> cartList) {
        for (Cart cart : cartList) {
            if (cart.getSellerId().equals(sellerId)) {
                return cart;
            }
        }
        return null;
    }


    @Override
    public List<Cart> mergeCartList(List<Cart> cookieList, List<Cart> redisList) {
        for(Cart cart: cookieList){
            for(TbOrderItem orderItem:cart.getOrderItemList()){
                redisList= addGoodsToCartList(redisList,orderItem.getItemId(),orderItem.getNum());
            }
        }
        return redisList;
    }
}
