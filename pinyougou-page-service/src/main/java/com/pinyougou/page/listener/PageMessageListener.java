package com.pinyougou.page.listener;

import com.alibaba.fastjson.JSON;
import com.pinyougou.common.pojo.MessageInfo;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.TbItem;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageMessageListener implements MessageListenerConcurrently {


    @Autowired
    private ItemPageService itemPageService;

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
        System.out.println(">>>>当前的线程>>>>" + Thread.currentThread().getName());
        try {
            if (msgs != null)
                //循环遍历
                for (MessageExt msg : msgs) {
                    //获取消息体
                    byte[] body = msg.getBody();
                    //转为字符串
                    String str = new String(body);
                    // 转成对象
                    MessageInfo info = JSON.parseObject(str, MessageInfo.class);
                    switch (info.getMethod()) {
                        //1 新增
                        case MessageInfo.METHOD_ADD: {

                            updatePageHtml(info);
                            break;
                        }
                        //2 更新 重新生成覆盖
                        case MessageInfo.METHOD_UPDATE: {
                            updatePageHtml(info);
                            break;
                        }
                        //3 删除
                        case MessageInfo.METHOD_DELETE: {
                            String s = info.getContext().toString();
                            //获取Long数组
                            Long[] longs = JSON.parseObject(s, Long[].class);
                            itemPageService.deleteById(longs);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            //直接返回消费成功,如果消费失败 就是消费延迟 会重新发送消息。
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            //直接返回消费成功,如果消费失败 就是消费延迟 会重新发送消息。
            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }

    }

    /**
     * 抽取通用方法
     * @param info 消息体
     */
    private void updatePageHtml(MessageInfo info) {
        String context1 = info.getContext().toString();//获取到的是Map对象 并不能直接序列化回来 需要直接转成字符串
        List<TbItem> tbItems = JSON.parseArray(context1, TbItem.class);
        //可以去重
        Set<Long> set = new HashSet<>();
        for (TbItem tbItem : tbItems) {
            //循环遍历进行生成静态页面。
            set.add(tbItem.getGoodsId());
        }
        //循环遍历 生成静态页面
        for (Long aLong : set) {
            //需要传入spuId
            itemPageService.genItemHtml(aLong);
        }
    }
}
