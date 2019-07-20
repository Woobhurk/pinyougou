package com.itheima;


import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * 消费者
 */
public class App {
    public static void main(String[] args) throws Exception {
        //1.创建一个消费者 指定一个消费者组
        //有push和pull根据业务需求来决定格式推还是拉
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group1_DLIAng");
        //2.设置nameserver地址
        //   consumer.setNamesrvAddr("192.168.25.129:9876");
      //  consumer.setNamesrvAddr("192.168.25.129:9876");
        consumer.setNamesrvAddr("192.168.25.129:9876");

        //3.设置主题 设置主题中的标签 (从哪个主题中的哪个标签取消息,和生产者一致)
        //如果想获取主题所有标签的信息可以使用*
        consumer.subscribe("TopicTest", "TagA");

        //4.设置消费者模式(集群还是广播 默认集群)
        consumer.setMessageModel(MessageModel.CLUSTERING);

        //5.设置监听器 监听消息 (可以设置是同时消费,还是顺序消费)
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            /**
             *
             * @param msgs   消息
             * @param context 线程的上下文
             * @return
             */
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                try {
                    if (msgs != null) {
                        //获取消息
                        //循环遍历
                        for (MessageExt msg : msgs) {
                            //获取消息体 字节数组
                            byte[] body = msg.getBody();
                            //把字节数组 转为字符串输出打印
                            String s = new String(body);
                            //打印
                            System.out.println(s);
                        }
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //如果出现异常,下次消费
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });
        //6.开启连接
        consumer.start();
        //7.关闭资源(一般系统宕机才会触发)
    }
}
