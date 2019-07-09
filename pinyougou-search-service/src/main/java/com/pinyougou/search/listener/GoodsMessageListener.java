package com.pinyougou.search.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/***
 * 监听器类作用
 * 1获取消息
 * 2获取消息内容转换数据
 * 3更新索引库
 * MessageListenerConcurrently 是并发执行的
 *
 */
public class GoodsMessageListener implements MessageListenerConcurrently {

    @Autowired
    private ItemSearchService itemSearchService;


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        try {
            System.out.println(">>>>>>>>>>>>>>>>>>接收数据");
            if (msgs != null) {
                //遍历消息
                for (MessageExt msg : msgs) {
                    //获取消息体转为字符串
                    byte[] body = msg.getBody();
                    String s = new String(body);
                    //将字符串转为对象
                    MessageInfo messageInfo = JSON.parseObject(s, MessageInfo.class);
                    //对象进行判断,根据类型执行对应 更新或删除
                    switch (messageInfo.getMethod()) {
                        case 1://新增
                        {
                            //因为传递的时候转为了字节数组,所以需要先转成字符串 然后在转为json对象
                            String context1 = messageInfo.getContext().toString();//获取到的是字符串
                            List<TbItem> tbItems = JSON.parseArray(context1, TbItem.class);
                            itemSearchService.updateIndex(tbItems);
                            break;
                        }

                        case 2://更新
                        {
                            String context1 = messageInfo.getContext().toString();//获取到的是字符串
                            List<TbItem> tbItems = JSON.parseArray(context1, TbItem.class);
                            itemSearchService.updateIndex(tbItems);
                            break;
                        }
                        case 3://删除
                        {
                            String s1 = messageInfo.getContext().toString();
                            Long[] longs = JSON.parseObject(s1, Long[].class);
                            itemSearchService.deleteByIds(longs);
                            break;
                        }
                        default: {
                            break;
                        }
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
