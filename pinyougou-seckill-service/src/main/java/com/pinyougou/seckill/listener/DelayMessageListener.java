package com.pinyougou.seckill.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class DelayMessageListener implements MessageListenerConcurrently {

    @Autowired
    private SeckillOrderService seckillOrderService;



    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

        try {
            if (msgs != null) {
                for (MessageExt msg : msgs) {
                    System.out.println("延时消息接收");
                    //1.获取消息体
                    byte[] body = msg.getBody();
                    //2.获取字符串
                    String s = new String(body);
                    //转为json
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);

                    //订单更新 标识即可
                    if (messageInfo.getMethod() == MessageInfo.METHOD_UPDATE) {
                        //获取Redis中的未支付订单的信息
                        TbSeckillOrder tbSeckillOrder = JSON.parseObject(messageInfo.getContext().toString(), TbSeckillOrder.class);
                        ////查询数据库中的数据  如果没有数据 ，没有支付
                        TbSeckillOrder seckillOrder = seckillOrderMapper.selectByPrimaryKey(tbSeckillOrder.getId());
                        if (seckillOrder == null) {
                            //关闭微信订单  如果 关闭微信订单的时候出现该订单已经关闭 则说明不需要 再恢复库存

                            /**
                             * if(微信订单的状态为没有关闭 或者 其他的错误){
                             *     调用删除订单 恢复库存的方法，
                             * }else{
                             *     啥也不干
                             * }
                             */
                            //删除订单
                            seckillOrderService.deleteOrder(tbSeckillOrder.getUserId());
                        }
                        //有订单 无需关心
                    }

                }
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
    }
    }

