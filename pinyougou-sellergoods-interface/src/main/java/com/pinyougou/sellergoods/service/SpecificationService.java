package com.pinyougou.sellergoods.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbSpecification;
import entity.Specification;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SpecificationService extends CoreService<TbSpecification> {


	void delete(Long[] ids);

	void update(Specification specification);

	Specification findOne(Long id);


	void add(Specification record);

	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSpecification> findPage(Integer pageNo, Integer pageSize, TbSpecification Specification);


    PageInfo<TbSpecification> findPageByOption(Integer pageNo, Integer pageSize, TbSpecification specification);

	void updateStatus(Long[] ids, String status);
}
