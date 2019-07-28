package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbUser;
import com.pinyougou.sellergoods.service.UserService;
import entity.ResultInfo;
import entity.UserParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserVc {
    @Reference
    private UserService userService;

    @RequestMapping("/findByPageParam/{pageNum}/{pageSize}")
    public ResultInfo findByPageParam(@PathVariable("pageNum") Integer pageNum,
        @PathVariable("pageSize") Integer pageSize, @RequestBody UserParam userParam) {
        ResultInfo resultInfo = new ResultInfo();

        resultInfo.setSuccess(true);
        resultInfo.setData(this.userService.findByPageParam(pageNum, pageSize, userParam));

        return resultInfo;
    }

    @RequestMapping("/update")
    public ResultInfo update(@RequestBody TbUser user) {
        ResultInfo resultInfo = new ResultInfo();

        resultInfo.setSuccess(true);
        resultInfo.setData(this.userService.updateByPrimaryKey(user));

        return resultInfo;
    }
}
