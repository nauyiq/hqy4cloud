package com.hqy.mq.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/10 14:36
 */
@Configuration
public class KafkaInitialConfiguration {

    /**
     * 创建一个名叫topic3的Topic 并且分区数是4 分区副本数是1
     * @return
     */
    @Bean
    public NewTopic topic3() {
        return new NewTopic("topic3", 4, (short) 1);
    }

    /**
     * 如果要修改分区数，只需修改配置值重启项目即可
     * 修改分区数并不会导致数据的丢失，但是分区数只能增大不能减小
     * @return
     */
    @Bean
    public NewTopic updateTopic() {
        return new NewTopic("topic3", 8, (short) 1);
    }


}
