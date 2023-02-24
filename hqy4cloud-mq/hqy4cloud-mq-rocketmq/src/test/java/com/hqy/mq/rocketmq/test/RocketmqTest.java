package com.hqy.mq.rocketmq.test;

import com.hqy.mq.common.bind.MessageParams;
import com.hqy.mq.rocketmq.server.RocketmqMessageProducer;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/10 16:51
 */
//@RunWith(SpringRunner.class)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource("classpath:bootstrap.yaml")
@ImportAutoConfiguration({ RocketMQAutoConfiguration.class})
public class RocketmqTest {
    @Autowired
    private RocketmqMessageProducer producer;


    @Test
    public void send() {
        producer.send(() -> new MessageParams("test", "test-tag"));
    }



}
