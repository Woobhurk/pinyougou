package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojo.TbAddress;
import com.pinyougou.pojo.TbAreas;
import com.pinyougou.pojo.TbCities;
import com.pinyougou.pojo.TbProvinces;
import com.pinyougou.user.service.AddressService;
import com.pinyougou.user.service.AreasService;
import com.pinyougou.user.service.CitiesService;
import com.pinyougou.user.service.ProvincesService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

	@Reference
	private AddressService addressService;

	@Reference
	private ProvincesService provincesService;

	@Reference
	private CitiesService citiesService;

	@Reference
	private AreasService areasService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbAddress> findAll(){			
		return addressService.findAll();
	}
	
	
	
	@RequestMapping("/findPage")
    public PageInfo<TbAddress> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize) {
        return addressService.findPage(pageNo, pageSize);
    }
	
	/**
	 * 增加
	 * @param address
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbAddress address){
		try {
			address.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());
			address.setIsDefault("0");
			addressService.add(address);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param address
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbAddress address){
		try {
			addressService.update(address);
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
	public TbAddress findOne(@PathVariable(value = "id") Long id){
		return addressService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(@RequestBody Long[] ids){
		try {
			addressService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	

	@RequestMapping("/search")
    public PageInfo<TbAddress> findPage(@RequestParam(value = "pageNo", defaultValue = "1", required = true) Integer pageNo,
                                      @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize,
                                      @RequestBody TbAddress address) {
        return addressService.findPage(pageNo, pageSize, address);
    }

    @RequestMapping("/findAddressListByUserId")
    public List<TbAddress> findAddressListByUserId(){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		TbAddress address = new TbAddress();
		address.setUserId(userId);
		return addressService.select(address);
	}


	@RequestMapping("/findProvincesList")
	public List<TbProvinces> findProvincesList (){
		return provincesService.findAll();
	}



	@RequestMapping("/findByCityId/{newval}")
	public List<TbCities> findByCityId (@PathVariable("newval")String id){
		TbCities tbCities = new TbCities();
		tbCities.setProvinceid(id);
		return citiesService.select(tbCities);
	}

	@RequestMapping("/findByAreasId/{newval}")
	public List<TbAreas> findByAreasId (@PathVariable("newval")String id){
		TbAreas tbAreas = new TbAreas();
		tbAreas.setCityid(id);
		return areasService.select(tbAreas);
	}




}
