package com.pinyougou.order.service.impl;
import java.math.BigDecimal;

import java.util.Date;
import java.util.List;

import com.pinyougou.common.constant.Constant;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import entity.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo; 									  
import org.apache.commons.lang3.StringUtils;
import com.pinyougou.core.service.CoreServiceImpl;

import org.springframework.data.redis.core.RedisTemplate;
import tk.mybatis.mapper.entity.Example;

import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.pojo.TbOrder;  



/**
 * 服务实现层
 * @author Administrator
 *
 */
@Service
public class OrderServiceImpl extends CoreServiceImpl<TbOrder>  implements OrderService {

	@Autowired
	private RedisTemplate redisTemplate;

	@Autowired
	private TbItemMapper itemMapper;
	
	private TbOrderMapper orderMapper;

	@Autowired
	private TbOrderItemMapper orderItemMapper;

	@Autowired
	private IdWorker idWorker;

	@Autowired
	public OrderServiceImpl(TbOrderMapper orderMapper) {
		super(orderMapper, TbOrder.class);
		this.orderMapper=orderMapper;
	}

	@Override
	public void add(TbOrder order) {

		//1.获取页面传递的数据

		//2.插入到订单表中 拆单(一个商家一个订单) 订单的ID使用雪花生成器生成

		//2.1通过userID查询redis中购物车的数据
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps(Constant.CART_REDIS_KEY).get(order.getUserId());
		//2.2循环遍历 每一个Cart对象 都是一个商家
		for (Cart cart : cartList) {
			//使用雪花 生成id
			long orderId = idWorker.nextId();
			System.out.println("sellerId:" + cart.getSellerId());
			TbOrder tborder = new TbOrder();//新创建订单对象
			tborder.setOrderId(orderId);//订单ID
			tborder.setUserId(order.getUserId());//用户名
			tborder.setPaymentType(order.getPaymentType());//支付类型
			tborder.setStatus("1");//状态：未付款
			tborder.setCreateTime(new Date());//订单创建日期
			tborder.setUpdateTime(new Date());//订单更新日期
			tborder.setReceiverAreaName(order.getReceiverAreaName());//地址
			tborder.setReceiverMobile(order.getReceiverMobile());//手机号
			tborder.setReceiver(order.getReceiver());//收货人
			tborder.setSourceType(order.getSourceType());//订单来源
			tborder.setSellerId(cart.getSellerId());//商家ID
			//循环购物车明细
			//金额不能直接使用购物车的总金额 ,要单独算出每一个商家的金额然后设置
			double money = 0;
			for (TbOrderItem orderItem : cart.getOrderItemList()) {
				//插入到订单选项表中 一个订单有多行商品 所以有多个订单选项
				orderItem.setId(idWorker.nextId());
				orderItem.setOrderId(orderId);//订单ID 订单选项属于哪个订单的 id
				orderItem.setSellerId(cart.getSellerId());
				TbItem item = itemMapper.selectByPrimaryKey(orderItem.getItemId());//商品
				orderItem.setGoodsId(item.getGoodsId());//设置商品的SPU的ID

				money += orderItem.getTotalFee().doubleValue();//金额累加
				orderItemMapper.insert(orderItem);
			}
			tborder.setPayment(new BigDecimal(money));
			orderMapper.insert(tborder);
		}
		//下单了购物车就不存在了 就需要删除redis中的购物数据
		//只需要删除当前用户下的购物车 不能把大的购物车删了 那整个网页的购物车都没了
		redisTemplate.boundHashOps(Constant.CART_REDIS_KEY).delete(order.getUserId());


		}



	@Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        List<TbOrder> all = orderMapper.selectAll();
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);

        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);
        return pageInfo;
    }

	
	

	 @Override
    public PageInfo<TbOrder> findPage(Integer pageNo, Integer pageSize, TbOrder order) {
        PageHelper.startPage(pageNo,pageSize);

        Example example = new Example(TbOrder.class);
        Example.Criteria criteria = example.createCriteria();

        if(order!=null){			
						if(StringUtils.isNotBlank(order.getPaymentType())){
				criteria.andLike("paymentType","%"+order.getPaymentType()+"%");
				//criteria.andPaymentTypeLike("%"+order.getPaymentType()+"%");
			}
			if(StringUtils.isNotBlank(order.getPostFee())){
				criteria.andLike("postFee","%"+order.getPostFee()+"%");
				//criteria.andPostFeeLike("%"+order.getPostFee()+"%");
			}
			if(StringUtils.isNotBlank(order.getStatus())){
				criteria.andLike("status","%"+order.getStatus()+"%");
				//criteria.andStatusLike("%"+order.getStatus()+"%");
			}
			if(StringUtils.isNotBlank(order.getShippingName())){
				criteria.andLike("shippingName","%"+order.getShippingName()+"%");
				//criteria.andShippingNameLike("%"+order.getShippingName()+"%");
			}
			if(StringUtils.isNotBlank(order.getShippingCode())){
				criteria.andLike("shippingCode","%"+order.getShippingCode()+"%");
				//criteria.andShippingCodeLike("%"+order.getShippingCode()+"%");
			}
			if(StringUtils.isNotBlank(order.getUserId())){
				criteria.andLike("userId","%"+order.getUserId()+"%");
				//criteria.andUserIdLike("%"+order.getUserId()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerMessage())){
				criteria.andLike("buyerMessage","%"+order.getBuyerMessage()+"%");
				//criteria.andBuyerMessageLike("%"+order.getBuyerMessage()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerNick())){
				criteria.andLike("buyerNick","%"+order.getBuyerNick()+"%");
				//criteria.andBuyerNickLike("%"+order.getBuyerNick()+"%");
			}
			if(StringUtils.isNotBlank(order.getBuyerRate())){
				criteria.andLike("buyerRate","%"+order.getBuyerRate()+"%");
				//criteria.andBuyerRateLike("%"+order.getBuyerRate()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverAreaName())){
				criteria.andLike("receiverAreaName","%"+order.getReceiverAreaName()+"%");
				//criteria.andReceiverAreaNameLike("%"+order.getReceiverAreaName()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverMobile())){
				criteria.andLike("receiverMobile","%"+order.getReceiverMobile()+"%");
				//criteria.andReceiverMobileLike("%"+order.getReceiverMobile()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiverZipCode())){
				criteria.andLike("receiverZipCode","%"+order.getReceiverZipCode()+"%");
				//criteria.andReceiverZipCodeLike("%"+order.getReceiverZipCode()+"%");
			}
			if(StringUtils.isNotBlank(order.getReceiver())){
				criteria.andLike("receiver","%"+order.getReceiver()+"%");
				//criteria.andReceiverLike("%"+order.getReceiver()+"%");
			}
			if(StringUtils.isNotBlank(order.getInvoiceType())){
				criteria.andLike("invoiceType","%"+order.getInvoiceType()+"%");
				//criteria.andInvoiceTypeLike("%"+order.getInvoiceType()+"%");
			}
			if(StringUtils.isNotBlank(order.getSourceType())){
				criteria.andLike("sourceType","%"+order.getSourceType()+"%");
				//criteria.andSourceTypeLike("%"+order.getSourceType()+"%");
			}
			if(StringUtils.isNotBlank(order.getSellerId())){
				criteria.andLike("sellerId","%"+order.getSellerId()+"%");
				//criteria.andSellerIdLike("%"+order.getSellerId()+"%");
			}
	
		}
        List<TbOrder> all = orderMapper.selectByExample(example);
        PageInfo<TbOrder> info = new PageInfo<TbOrder>(all);
        //序列化再反序列化
        String s = JSON.toJSONString(info);
        PageInfo<TbOrder> pageInfo = JSON.parseObject(s, PageInfo.class);

        return pageInfo;
    }
	
}
