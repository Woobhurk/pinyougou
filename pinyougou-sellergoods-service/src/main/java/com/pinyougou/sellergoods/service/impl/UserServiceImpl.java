package com.pinyougou.sellergoods.service.impl;

import java.util.Date;
import java.util.List;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbUserMapper;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.service.UserService;
import entity.PageResult;
import entity.UserParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserServiceImpl extends CoreServiceImpl<TbUser> implements UserService {
    private TbUserMapper userMapper;

    @Autowired
    public UserServiceImpl(TbUserMapper userMapper) {
        super(userMapper, TbUser.class);
        this.userMapper = userMapper;
    }

    @Override
    public PageResult<TbUser> findByPageParam(Integer pageNum, Integer pageSize,
        UserParam userParam) {
        Example example = new Example(TbUser.class);
        Example.Criteria criteria = example.createCriteria();
        List<TbUser> savedUserList;

        if (StringUtils.isNotBlank(userParam.getUsername())) {
            criteria.andLike("username", "%" + userParam.getUsername() + "%");
        }

        if (userParam.getStartTime() != null) {
            criteria.andGreaterThanOrEqualTo("created", userParam.getStartTime());
        }

        if (userParam.getEndTime() != null) {
            criteria.andLessThanOrEqualTo("created", userParam.getEndTime());
        }

        if (StringUtils.isNotBlank(userParam.getStatus())) {
            criteria.andEqualTo("status", userParam.getStatus());
        }

        if (userParam.getUnfrozen() != null) {
            if (userParam.getUnfrozen()) {
                criteria.andIsNull("unfrozen");
                criteria.orLessThanOrEqualTo("unfrozen", new Date());
            } else {
                criteria.andGreaterThan("unfrozen", new Date());
            }
        }

        savedUserList = this.userMapper.selectByExample(example);

        return new PageResult<>(savedUserList, pageNum, pageSize);
    }

    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize) {
        return null;
    }

    @Override
    public PageInfo<TbUser> findPage(Integer pageNo, Integer pageSize, TbUser User) {
        return null;
    }
}
