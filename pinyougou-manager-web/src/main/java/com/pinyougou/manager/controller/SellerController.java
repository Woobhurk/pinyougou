package com.pinyougou.manager.controller;

import java.util.List;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSeller;
import com.pinyougou.sellergoods.service.SellerService;
import entity.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSeller> findAll() {
        return this.sellerService.findAll();
    }



    @RequestMapping("/findPage")
    public PageInfo<TbSeller> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return this.sellerService.findPage(pageNo, pageSize);
    }

    /**
     * 增加
     *
     * @param seller
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbSeller seller) {
        try {
			this.sellerService.add(seller);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param seller
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbSeller seller) {
        try {
			this.sellerService.update(seller);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne/{id}")
    public TbSeller findOne(@PathVariable(value = "id") String id) {
        return this.sellerService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(@RequestBody Long[] ids) {
        try {
			this.sellerService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }



    @RequestMapping("/search")
    public PageInfo<TbSeller> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
        @RequestBody TbSeller seller) {
        return this.sellerService.findPage(pageNo, pageSize, seller);
    }


    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status) {
        try {
			this.sellerService.updateStatus(sellerId, status);
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "失败");
        }

    }



}
