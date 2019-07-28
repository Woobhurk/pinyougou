package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbUser;
import entity.PageResult;
import entity.UserParam;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface UserService extends CoreService<TbUser> {

    PageResult<TbUser> findByPageParam(Integer pageNum, Integer pageSize, UserParam userParam);

    /**
     * 返回分页列表
     *
     * @return
     */
    PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize);



    /**
     * 分页
     *
     * @param pageNo 当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser User);

}
