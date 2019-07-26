package com.pinyougou.sellergoods.voservice.impl;

import java.util.ArrayList;
import java.util.List;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.sellergoods.voservice.OrderItemChartService;
import entity.OrderItemChart;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
@org.springframework.stereotype.Service
public class OrderItemChartServiceImpl implements OrderItemChartService {
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Override
    public List<OrderItemChart> countOrderItem() {
        OrderItemChart orderItemChart = new OrderItemChart();
        OrderItemChart newOrderItemChart;

        orderItemChart.setParentId(0L);
        newOrderItemChart = this.retrieveOrderChart(orderItemChart);

        return newOrderItemChart.getChildren();
    }

    /************************************************************************/
    private OrderItemChart retrieveOrderChart(OrderItemChart orderItemChart) {
        Long parentId = (orderItemChart.getParentId() == null) ? 0L
            : orderItemChart.getParentId();
        Example example = new Example(TbItemCat.class);
        Example.Criteria criteria = example.createCriteria();
        List<TbItemCat> itemCatList;

        // 查找下级分类
        criteria.andEqualTo("parentId", parentId);
        itemCatList = this.itemCatMapper.selectByExample(example);

        if (itemCatList == null || itemCatList.isEmpty()) {
            // 没有下级分类，查找对应订单总金额
            System.err.println("=========== NODE");
        } else {
            // 有下级分类，继续递归查找
            List<OrderItemChart> subOrderItemChartList = new ArrayList<>();

            for (TbItemCat itemCat : itemCatList) {
                OrderItemChart subOrderItemChart = new OrderItemChart();

                subOrderItemChart.setParentId(itemCat.getId());
                subOrderItemChart.setName(itemCat.getName());
                subOrderItemChartList.add(this.retrieveOrderChart(subOrderItemChart));
            }

            System.err.println("PARENT " + orderItemChart.getParentId());
            System.err.println("CHILDREN " + subOrderItemChartList);
            orderItemChart.setChildren(subOrderItemChartList);

            //for (OrderItemChart subOrderItemChart : subOrderItemChartList) {
            //
            //}
        }

        return orderItemChart;
    }

    //private List<OrderItemChart> retrieveCategory1List(List<OrderItemChart> orderItemChartList) {
    //    Example example = new Example(TbItemCat.class);
    //    Example.Criteria criteria = example.createCriteria();
    //    List<TbItemCat> itemCat1List;
    //
    //    criteria.andEqualTo("parentId", 0);
    //    itemCat1List = this.itemCatMapper.selectByExample(example);
    //
    //    for (TbItemCat itemCat1 : itemCat1List) {
    //        OrderItemChart orderItemChart = new OrderItemChart();
    //
    //        orderItemChart.setName(itemCat1.getName());
    //        orderItemChartList.add(orderItemChart);
    //    }
    //
    //    return orderItemChartList;
    //}
    //
    //private List<OrderItemChart> retrieveCategory2List(List<OrderItemChart> orderItemChartList,
    //    List<TbItemCat> itemCat1List) {
    //    Example example = new Example(TbItemCat.class);
    //    Example.Criteria criteria = example.createCriteria();
    //    List<TbItemCat> itemCat2List;
    //
    //    for (int i = 0; i < itemCat1List.size(); i++) {
    //        List<OrderItemChart> orderItemChartList2 = new ArrayList<>();
    //
    //        criteria.andEqualTo("parentId", itemCat1List.get(i).getId());
    //        itemCat2List = this.itemCatMapper.selectByExample(example);
    //
    //        for (TbItemCat itemCat2 : itemCat2List) {
    //            OrderItemChart orderItemChart = new OrderItemChart();
    //
    //            orderItemChart.setName(itemCat2.getName());
    //            orderItemChartList2.add(orderItemChart);
    //        }
    //
    //        orderItemChartList.get(i).setChildren(orderItemChartList2);
    //    }
    //
    //    return orderItemChartList;
    //}
    //
    //private List<OrderItemChart> retrieveCategory3List(List<OrderItemChart> orderItemChartList,
    //    List<TbItemCat> itemCat2List) {
    //    Example example = new Example(TbItemCat.class);
    //    Example.Criteria criteria = example.createCriteria();
    //    List<TbItemCat> itemCat3List;
    //
    //    for (int i = 0; i < itemCat2List.size(); i++) {
    //        List<OrderItemChart> orderItemChartList3 = new ArrayList<>();
    //
    //        criteria.andEqualTo("parentId", itemCat2List.get(i).getId());
    //        itemCat3List = this.itemCatMapper.selectByExample(example);
    //
    //        for (TbItemCat itemCat3 : itemCat3List) {
    //            OrderItemChart orderItemChart = new OrderItemChart();
    //
    //            orderItemChart.setName(itemCat3.getName());
    //            orderItemChartList3.add(orderItemChart);
    //        }
    //
    //        orderItemChartList.get(i).setChildren(orderItemChartList3);
    //    }
    //
    //    return orderItemChartList;
    //}
    //
    //private List<OrderItemChart> countPriceOfCategory(List<OrderItemChart> orderItemChartList) {
    //
    //    return orderItemChartList;
    //}
    //
    //private List<Integer> splitIds(List<TbItemCat> itemCatList) {
    //    return null;
    //}
}
