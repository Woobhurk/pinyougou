package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbOrder;
import entity.OrderParam;
import entity.OrderResult;
import entity.PageResult;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface OrderService extends CoreService<TbOrder> {
    PageResult<OrderResult> findByPageParam(Integer pageNum, Integer pageSize,
        OrderParam orderParam);

    PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize);

    PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder Order);
}
