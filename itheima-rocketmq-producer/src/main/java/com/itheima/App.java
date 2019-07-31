package com.itheima;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * 生产者
 */
public class App {

    public static void main(String[] args) throws Exception {
        //1.创建生产者 对象 并指定组名
        DefaultMQProducer producer = new DefaultMQProducer("producer_cluster_group1_DLIAng");
        // producer.setVipChannelEnabled(false);
        //2.设置 nameserver的地址
        //producer.setNamesrvAddr("192.168.146.130:9876");
       //producer.setNamesrvAddr("192.168.146.130:9876");
        producer.setNamesrvAddr("192.168.146.130:9876");
        //3.开启连接 并使用
        producer.start();
        //4.发送消息
        for (int i = 0; i < 2; i++) {
            //创建消息对象，并指定主题 标签 和消息体
            /**
             *  参数1.主题名称
             *  参数2.指定的标签名称
             *  参数3.key指定业务上的唯一标示(可以不填)
             *  参数4.指定消息体,消息的内容本身
             */
            Message msg = new Message("TopicTest",
                    "TagA","啊啊啊啊",
                    ("Hello RocketMQ" + "你好：" + i).getBytes(RemotingHelper.DEFAULT_CHARSET) /* Message body */
            );
            //Call send message to deliver message to one of brokers.
            //发送消息到其中的一个broker中
            SendResult sendResult = producer.send(msg, 10000);
            System.out.printf("%s%n", sendResult);
        }
        //关闭资源
        producer.shutdown();

    }
}
