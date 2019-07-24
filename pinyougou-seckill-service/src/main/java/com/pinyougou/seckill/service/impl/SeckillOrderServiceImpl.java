package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.constant.Constant;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.core.service.CoreServiceImpl;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import com.pinyougou.seckill.thread.OrderHandler;
import entity.SeckillStatus;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class SeckillOrderServiceImpl extends CoreServiceImpl<TbSeckillOrder>  implements SeckillOrderService {


	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	private OrderHandler orderHandler;



	private TbSeckillOrderMapper seckillOrderMapper;



	@Autowired
	public SeckillOrderServiceImpl(TbSeckillOrderMapper seckillOrderMapper) {
		super(seckillOrderMapper, TbSeckillOrder.class);
		this.seckillOrderMapper=seckillOrderMapper;
	}


	@Override
	public void submitOrder(Long seckillId, String userId) {
		//从nosql数据库中获取商品
		TbSeckillGoods killgoods = (TbSeckillGoods) redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).get(seckillId);

		//if (killgoods == null || killgoods.getStockCount() <= 0) {
		//	//说明商品已经没有库存了
		//	throw new RuntimeException("商品已被抢光");
		//}
		//先判断用户是否在排队中 如果在排队直接提示 不创建订单
		Object o2 = redisTemplate.boundHashOps(Constant.SEC_USER_QUEUE_FLAG_KEY).get(userId);
		if (o2!=null) {
			throw new RuntimeException("您正在排队中");
		}


		//进行判断 从redis中查询当前用户是否有未支付的订单 如果有不让其继续下单
		Object o1 = redisTemplate.boundHashOps(Constant.SEC_KILL_ORDER).get(userId);
		if (o1!=null) {
			throw new RuntimeException("有未支付的订单");
		}


		//从商品队列中弹出一个
		Object o = redisTemplate.boundListOps(Constant.SEC_KILL_GOODS_PREFIX + seckillId).rightPop();
		if (o==null) {
			//如果弹不出来就证明没有库存卖完了
			throw new RuntimeException("商品已被抢光");
		}
		//压入队列
		redisTemplate.boundListOps(Constant.SEC_KILL_USER_ORDER_LIST).leftPush(new SeckillStatus(userId, seckillId, SeckillStatus.SECKILL_queuing));
		//此时应该表示用户只能进入队列中 不能保证一定创建订单成功
		//用来标记该用户进入排队中
		redisTemplate.boundHashOps(Constant.SEC_USER_QUEUE_FLAG_KEY).put(userId,seckillId);

		//调用多线程方法执行下单
		orderHandler.handleOrder();

	}

	@Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbSeckillOrder> all = seckillOrderMapper.selectAll();
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbSeckillOrder> findPage(Integer pageNo, Integer pageSize, TbSeckillOrder seckillOrder) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbSeckillOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(seckillOrder!=null){			
						if(StringUtils.isNotBlank(seckillOrder.getUserId())){
				criteria.andLike("userId","%"+seckillOrder.getUserId()+"%");
				//criteria.andUserIdLike("%"+seckillOrder.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getSellerId())){
				criteria.andLike("sellerId","%"+seckillOrder.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+seckillOrder.getSellerId()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getStatus())){
				criteria.andLike("status","%"+seckillOrder.getStatus()+"%");
				//criteria.andStatusLike("%"+seckillOrder.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiverAddress())){
				criteria.andLike("receiverAddress","%"+seckillOrder.getReceiverAddress()+"%");
				//criteria.andReceiverAddressLike("%"+seckillOrder.getReceiverAddress()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiverMobile())){
				criteria.andLike("receiverMobile","%"+seckillOrder.getReceiverMobile()+"%");
				//criteria.andReceiverMobileLike("%"+seckillOrder.getReceiverMobile()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getReceiver())){
				criteria.andLike("receiver","%"+seckillOrder.getReceiver()+"%");
				//criteria.andReceiverLike("%"+seckillOrder.getReceiver()+"%");
			}
			if(StringUtils.isNotBlank(seckillOrder.getTransactionId())){
				criteria.andLike("transactionId","%"+seckillOrder.getTransactionId()+"%");
				//criteria.andTransactionIdLike("%"+seckillOrder.getTransactionId()+"%");
			}
	
		}
        List<TbSeckillOrder> all = seckillOrderMapper.selectByExample(example);
        PageInfo<TbSeckillOrder> info = new PageInfo<TbSeckillOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbSeckillOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }

	@Override
	public TbSeckillOrder getUserOrderStatus(String userId) {
		//根据用户id从redis中查找有没有该订单 返回
		return (TbSeckillOrder) redisTemplate.boundHashOps(Constant.SEC_KILL_ORDER).get(userId);
	}

	@Override
	public void updateOrderStatus(String transaction_id, String userId) {
		//1.根据用户的id获取订单
		TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(Constant.SEC_KILL_ORDER).get(userId);
		//订单不为空才进行更新
		if (seckillOrder != null) {
			//更新订单的数据
			seckillOrder.setPayTime(new Date());
			seckillOrder.setStatus("1");
			seckillOrder.setTransactionId(transaction_id);
			//把更新的信息存储到数据库中
			seckillOrderMapper.insert(seckillOrder);

			//删除预订单即可
			redisTemplate.boundHashOps(Constant.SEC_KILL_ORDER).delete(userId);
		}


	}

	@Override
	public void deleteOrder(String userId) {
		//获取redis中存储的订单
		TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(Constant.SEC_KILL_ORDER).get(userId);
		if (tbSeckillOrder!=null) {
			//1.恢复redis中的库存
			//获取商品id
			Long seckillId = tbSeckillOrder.getSeckillId();
			//通过商品id获取redis中存储的商品
			TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).get(seckillId);
			if (tbSeckillGoods==null) {
				//因为最后一个商品被买了后 会删除redis中的数据 所以有可能为空
				//需要从数据库中查询 然后在把 数据设置到redis中
				TbSeckillGoods tbSeckillGoods1 = seckillGoodsMapper.selectByPrimaryKey(seckillId);
				tbSeckillGoods1.setStockCount(1);
				redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).put(seckillId, tbSeckillGoods1);
				//把数据更新到数据库
				seckillGoodsMapper.updateByPrimaryKeySelective(tbSeckillGoods1);
			}else {
				//增加商品库存数
				tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()+1);
				//更新redis
				redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).put(seckillId, tbSeckillGoods);
			}
			//2.删除预订单
			redisTemplate.boundHashOps(Constant.SEC_KILL_ORDER).delete(userId);
			//4.恢复列队个数
			redisTemplate.boundListOps(Constant.SEC_KILL_GOODS_PREFIX+seckillId).leftPush(seckillId);

		}


	}

}
