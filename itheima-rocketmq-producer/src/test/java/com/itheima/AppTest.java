package com.itheima;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.junit.Test;
import org.apache.rocketmq.common.message.Message;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */
@ContextConfiguration("classpath:spring/producer.xml")
@RunWith(SpringRunner.class)
public class AppTest {


    @Autowired
    private DefaultMQProducer producer;
    //发送消息
    @Test
    public void sendMessage() throws Exception {
        //消息
        byte[] bytes = new String("nihao").getBytes();
        //设置主题 设置主题中的标签
        Message message = new Message("springTopic","TagB","唯一标示",bytes);
        //发送消息
        SendResult result = producer.send(message);
        System.out.println(result.getMsgId()+">>>>"+result.getSendStatus()+";>>>");
        Thread.sleep(10000);

    }
}
