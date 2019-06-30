package com.pinyougou.sellergoods.service;

import com.pinyougou.pojo.TbGoods;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import entity.Goods;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface GoodsService extends CoreService<TbGoods> {



    /**
     * 更新
     * @param goods
     * @return
     */

    void update(Goods goods);
    /**
     * 根据ID获取实体
     * @param id
     * @return
     */
    Goods findOne(Long id);

    void add(Goods goods);

    /**
     * 返回分页列表
     *
     * @return
     */
    PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize);


    /**
     * 分页
     *
     * @param pageNo   当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    PageInfo<TbGoods> findPage(Integer pageNo, Integer pageSize, TbGoods Goods);

    /**
     * 修改审核状态
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids, String status);
}
