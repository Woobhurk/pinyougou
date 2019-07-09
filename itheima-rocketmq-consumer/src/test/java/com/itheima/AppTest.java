package com.itheima;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */
@ContextConfiguration("classpath:spring/consumer.xml")
@RunWith(SpringRunner.class)
public class AppTest {
    @Autowired
    private DefaultMQPushConsumer consumer;


    //接收消息
    @Test
    public void shouldAnswerWithTrue() throws Exception {
        Thread.sleep(1000000);



    }
}
