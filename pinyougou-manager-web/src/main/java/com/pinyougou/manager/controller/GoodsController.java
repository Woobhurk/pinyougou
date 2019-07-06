package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.pojo.TbGoods;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import com.pinyougou.sellergoods.service.GoodsService;
import entity.Goods;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *  商品controller
 * @author Administrator
 *
 */

@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;

	//引入搜索依赖
	@Reference
	private ItemSearchService itemSearchService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){			
		return goodsService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return goodsService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {
			//获取用户名 商家id
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(sellerId);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody  Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne/{id}")
	public Goods findOne(@PathVariable(value = "id") Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			goodsService.delete(ids);
			//调用搜索服务 执行 删除ES数据
			itemSearchService.deleteByIds(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbGoods> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbGoods goods) {

        return goodsService.findPage(pageNo, pageSize, goods);

    }


    @RequestMapping("/updateStatus/{status}")
	public Result updateStatus (@RequestBody Long[] ids,@PathVariable(value = "status") String status){

		try {
			goodsService.updateStatus(ids,status);
			//调用搜索服务的方法 实现同步 更新
			//把更新的数据查出来  然后把更新的数据存入es中
			//重点就是要看审核的到底是哪个表
			if (status.equals("1")){
				//审核通过
				//1.根据审核的SPU的ID获取SKU的列表数据 因为ES的数据就是sku的数据
				List<TbItem> tbItemList = goodsService.findTbItemListByIds(ids);

				//2.调用搜索服务的方法(传递SKU的列表数据过去) 内部执行更新的动作
				itemSearchService.updateIndex(tbItemList);




			}

			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}
	}




}
