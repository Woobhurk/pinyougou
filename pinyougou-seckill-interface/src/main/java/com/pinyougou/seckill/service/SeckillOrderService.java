package com.pinyougou.seckill.service;

import com.github.pagehelper.PageInfo;
import com.pinyougou.core.service.CoreService;
import com.pinyougou.pojo.TbSeckillOrder;
/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface SeckillOrderService extends CoreService<TbSeckillOrder> {


	/**
	 * 秒杀下单
	 * @param seckillId 秒杀商品的ID
	 * @param userId 下单的用户ID
	 */
	  void submitOrder(Long seckillId,String userId);
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	 PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize);
	
	

	/**
	 * 分页
	 * @param pageNo 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder SeckillOrder);



	TbSeckillOrder getUserOrderStatus(String userId);

	void updateOrderStatus(String transaction_id, String userId);

	void deleteOrder(String userId);
}
