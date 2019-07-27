package com.pinyougou.sellergoods.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.sellergoods.service.OrderService;
import entity.OrderParam;
import entity.OrderResult;
import entity.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@org.springframework.stereotype.Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder> implements OrderService {
    private TbOrderMapper orderMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    public OrderServiceImpl(TbOrderMapper orderMapper) {
        super(orderMapper, TbOrder.class);
        this.orderMapper = orderMapper;
    }

    @Override
    public PageResult<OrderResult> findByPageParam(Integer pageNum, Integer pageSize,
        OrderParam orderParam) {
        Map<Long, List<TbOrderItem>> orderResultMap = new HashMap<>();
        List<TbOrderItem> orderItemList;
        List<OrderResult> orderResultList = new ArrayList<>();

        if (StringUtils.isBlank(orderParam.getTitle())) {
            orderParam.setTitle(null);
        }

        if (StringUtils.isBlank(orderParam.getStatus())) {
            orderParam.setStatus(null);
        }

        if (orderParam.getStartTime() == null) {
            orderParam.setStartTime(new Date(0));
        }

        if (orderParam.getEndTime() == null) {
            orderParam.setEndTime(new Date());
        }

        orderItemList = this.orderItemMapper.findByParam(orderParam);

        for (TbOrderItem orderItem : orderItemList) {
            if (!orderResultMap.containsKey(orderItem.getOrderId())) {
                orderResultMap.put(orderItem.getOrderId(), new ArrayList<>());
            }

            orderResultMap.get(orderItem.getOrderId()).add(orderItem);
        }

        orderResultMap.forEach((orderId, savedOrderItemList) -> {
            OrderResult orderResult = new OrderResult();
            TbOrder order = this.orderMapper.selectByPrimaryKey(orderId);

            orderResult.setOrder(order);
            orderResult.setOrderItemList(savedOrderItemList);
            orderResultList.add(orderResult);
        });

        return new PageResult<>(orderResultList, pageNum, pageSize);
    }

    @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize) {
        return null;
    }

    @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder Order) {
        return null;
    }
}
