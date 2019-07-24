package com.pinyougou.seckill.thread;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.constant.Constant;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import entity.SeckillStatus;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import javax.annotation.Resource;
import java.util.Date;

public class OrderHandler {

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private DefaultMQProducer defaultMQProducer;


    //多线程执行的代码逻辑
    @Async
    public void handleOrder() {
        System.out.println(Thread.currentThread().getName() + "==========开始耗时操作");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() + "==========结束耗时操作");
        //从队列中获取元素
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(Constant.SEC_KILL_USER_ORDER_LIST).rightPop();
        //判断队列是否能弹出值
        if (seckillStatus != null) {

            //获取用户id和秒杀商品的id
            TbSeckillGoods killgoods = (TbSeckillGoods) redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).get(seckillStatus.getGoodsId());
            //将这个商品的库存减少
            killgoods.setStockCount(killgoods.getStockCount() - 1);//减少

            redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).put(seckillStatus.getGoodsId(), killgoods);

            if (killgoods.getStockCount() <= 0) {//如果已经被秒光
                seckillGoodsMapper.updateByPrimaryKey(killgoods);//同步到数据库
                redisTemplate.boundHashOps(Constant.SEC_KILL_GOODS).delete(seckillStatus.getGoodsId());//将redis中的该商品清除掉
            }
            //创建订单
            long orderId = idWorker.nextId();

            TbSeckillOrder seckillOrder = new TbSeckillOrder();

            seckillOrder.setId(orderId);//设置订单的ID 这个就是out_trade_no
            seckillOrder.setCreateTime(new Date());//创建时间
            seckillOrder.setMoney(killgoods.getCostPrice());//秒杀价格  价格
            seckillOrder.setSeckillId(seckillStatus.getGoodsId());//秒杀商品的ID
            seckillOrder.setSellerId(killgoods.getSellerId());
            seckillOrder.setUserId(seckillStatus.getUserId());//设置用户ID
            seckillOrder.setStatus("0");//状态 未支付
            //将构建的订单保存到redis中
            redisTemplate.boundHashOps(Constant.SEC_KILL_ORDER).put(seckillStatus.getUserId(), seckillOrder);

            //订单创建成功 移除排队的标记
            redisTemplate.boundHashOps(Constant.SEC_USER_QUEUE_FLAG_KEY).delete(seckillStatus.getUserId());

            //发送延迟消息
            sendMessage(seckillOrder);

        }

    }

    private void sendMessage(TbSeckillOrder seckillOrder) {
        try {
            MessageInfo messageInfo = new MessageInfo("TOPIC_SECKILL_DELAY", "TAG_SECKILL_DELAY", "handleOrder_DELAY", seckillOrder, MessageInfo.METHOD_UPDATE);
            //
            System.out.println("多线程下单============");
            Message message = new Message(messageInfo.getTopic(), messageInfo.getTags(), messageInfo.getKeys(), JSON.toJSONString(messageInfo).getBytes());
            //1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
            //设置消息演示等级 16=30m
            message.setDelayTimeLevel(5);
            defaultMQProducer.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
