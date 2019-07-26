package com.pinyougou.sellergoods.voservice.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.voservice.OrderItemChartService;
import entity.OrderItemChart;
import entity.OrderItemParam;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
@org.springframework.stereotype.Service
public class OrderItemChartServiceImpl implements OrderItemChartService {
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemChart> countOrderItem(OrderItemParam orderItemParam) {
        OrderItemChart orderItemChart = new OrderItemChart();
        OrderItemChart newOrderItemChart;

        orderItemChart.setItemCatId(orderItemParam.getParentId());
        newOrderItemChart = this.retrieveOrderChart(orderItemChart, orderItemParam);

        return newOrderItemChart.getChildren();
    }

    /**********************************************************************/
    private OrderItemChart retrieveOrderChart(OrderItemChart orderItemChart,
        OrderItemParam orderItemParam) {
        Long parentId = (orderItemChart.getItemCatId() == null) ? 0L
            : orderItemChart.getItemCatId();
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        List<TbItemCat> itemCatList;

        // 查找下级分类
        criteria.andEqualTo("parentId", parentId);
        itemCatList = this.itemCatMapper.selectByExample(example);

        if (itemCatList == null || itemCatList.isEmpty()) {
            // 没有下级分类，查询该分类订单总金额
            Double price;

            orderItemParam.setParentId(orderItemChart.getItemCatId());
            price = this.calculateOrderItemPrice(orderItemParam);
            orderItemChart.setValue(price);
        } else {
            // 有下级，继续递归查找
            List<OrderItemChart> subOrderItemChartList;
            Double price;

            subOrderItemChartList = this.retrieveSubOrderItemChartList(itemCatList,
                orderItemParam);
            orderItemChart.setChildren(subOrderItemChartList);
            price = this.calculateOrderItemTotalPrice(orderItemChart);
            orderItemChart.setValue(price);
        }

        return orderItemChart;
    }

    private List<OrderItemChart> retrieveSubOrderItemChartList(List<TbItemCat> itemCatList,
        OrderItemParam orderItemParam) {
        List<OrderItemChart> subOrderItemChartList = new ArrayList<>();

        for (TbItemCat itemCat : itemCatList) {
            OrderItemChart subOrderItemChart = new OrderItemChart();

            subOrderItemChart.setItemCatId(itemCat.getId());
            subOrderItemChart.setName(itemCat.getName());
            //subOrderItemChart.setValue(new Random().nextDouble() * 1000);
            subOrderItemChartList.add(
                this.retrieveOrderChart(subOrderItemChart, orderItemParam));
        }

        return subOrderItemChartList;
    }

    private Double calculateOrderItemPrice(OrderItemParam orderItemParam) {
        Double price;

        if (orderItemParam.getParentId() == null
            || orderItemParam.getParentId() < 0) {
            orderItemParam.setParentId(0L);
        }

        if (orderItemParam.getStartTime() == null) {
            orderItemParam.setStartTime(new Date(0L));
        }

        if (orderItemParam.getEndTime() == null) {
            orderItemParam.setEndTime(new Date());
        }

        price = this.orderItemMapper.countPriceOfCategory(orderItemParam);

        return (price == null) ? 0.0 : price;
    }

    private Double calculateOrderItemTotalPrice(OrderItemChart orderItemChart) {
        List<OrderItemChart> children = orderItemChart.getChildren();
        Double price;

        if (children == null || children.isEmpty()) {
            price = orderItemChart.getValue();
        } else {
            price = 0.0;

            for (OrderItemChart subOrderItemChart : children) {
                price += this.calculateOrderItemTotalPrice(subOrderItemChart);
            }
        }

        return price;
    }
}
