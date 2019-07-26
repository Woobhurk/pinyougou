package com.pinyougou.manager.controller;

import java.util.List;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.sellergoods.service.SpecificationService;
import entity.Result;
import entity.Specification;
import org.springframework.web.bind.annotation.*;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/specification")
public class SpecificationController {

    @Reference
    private SpecificationService specificationService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbSpecification> findAll() {
        return this.specificationService.findAll();
    }



    @RequestMapping("/findPage")
    public PageInfo<TbSpecification> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return this.specificationService.findPage(pageNo, pageSize);
    }

    /**
     * 增加
     *
     * @param specification
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody Specification specification) {
        try {
            this.specificationService.add(specification);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param specification
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody Specification specification) {
        try {

            this.specificationService.update(specification);
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
    @RequestMapping("/findOne")
    public Specification findOne(Long id) {
        return this.specificationService.findOne(id);
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
            this.specificationService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }



    @RequestMapping("/search")
    public PageInfo<TbSpecification> findPage(
        @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
        @RequestBody TbSpecification specification) {
        return this.specificationService.findPage(pageNo, pageSize, specification);
    }

    /**
     * 返回规格选项列表
     * @param pageNo
     * @param pageSize
     * @param specification
     * @return
     */
    @RequestMapping("/searchByOption")
    public PageInfo<TbSpecification> findPageByOption(
            @RequestParam(value = "pageNo", defaultValue = "1", required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) Integer pageSize,
            @RequestBody TbSpecification specification) {
        return this.specificationService.findPageByOption(pageNo, pageSize, specification);
    }

    /**
     * 更新规格选项表的status
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus/{status}")
    public Result updateStatus(@RequestBody Long[] ids, @PathVariable(value = "status") String status) {

        try {
            specificationService.updateStatus(ids,status);
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

}
