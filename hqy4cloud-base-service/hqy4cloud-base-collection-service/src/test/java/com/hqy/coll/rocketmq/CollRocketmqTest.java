package com.hqy.coll.rocketmq;

import com.hqy.coll.rocketmq.server.TestRocketmqMessage;
import com.hqy.mq.rocketmq.server.RocketmqMessageProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/13 9:21
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class CollRocketmqTest {

    @Autowired
    private RocketmqMessageProducer producer;

    @Test
    public void testSend() throws InterruptedException {
        producer.send(new TestRocketmqMessage());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        countDownLatch.await();
    }







}
