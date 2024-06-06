package com.hqy.cloud.gateway;

import cn.hutool.core.util.RandomUtil;
import com.hqy.cloud.foundation.redis.stream.RedisStreamService;
import com.hqy.cloud.foundation.redis.stream.support.RedisStreamMessageProducer;
import com.hqy.cloud.stream.api.StreamProducer;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/19
 */
@SpringBootTest( webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StreamTest {

    private static final String STREAM = "test";
    private static final String GROUP = "test-consumer-group1";
    private static final String GROUP2 = "test-consumer-group2";
    private static final String NAME = "consumer1";
    private static final String NAME2 = "consumer2";


    @Test
    void test_send_message(@Autowired RedisStreamService redisStreamService, RedisStreamMessageProducer redisStreamProducer) {
        User user = new User(RandomUtil.randomString(4), RandomUtil.randomString(4));
        // 添加消息， 不指定ID
        for (int i = 0; i < 2; i++) {
            String add = redisStreamService.add(STREAM, user);
        }
    }

    @Test
    void read_message(@Autowired RedisStreamService redisStreamService, @Autowired StreamProducer streamProducer) {
        // 创建消费者组
        try {
            // 表示从最新的消息偏移量开始创建 即$
            redisStreamService.createConsumerGroup(STREAM, GROUP);
        } catch (Throwable cause) {
            // 表示消费组已经创建了。。 不处理
        }

        // 创建消费者组
        try {
            // >
            redisStreamService.createConsumerGroup(STREAM, GROUP2);
        } catch (Throwable cause) {
            // 表示消费组已经创建了。。 不处理
            System.out.println(cause.getMessage());
        }


        // 消费组1不自动ACK读取消息， 并且设置某个开始的偏移量
        String begin = "1713776513570-0";
        List<ObjectRecord<String, User>> records = redisStreamService.readGroupWithRecord(GROUP, NAME, User.class,
                StreamReadOptions.empty(),  StreamOffset.create(STREAM, ReadOffset.from(begin)));
        System.out.println(records);


        // 消费组2自动ACK读取消息
        List<ObjectRecord<String, User>> objectRecords = redisStreamService.readGroupWithRecord(GROUP2, NAME2, User.class,
                StreamReadOptions.empty().autoAcknowledge(), StreamOffset.create(STREAM, ReadOffset.lastConsumed()));
        System.out.println(objectRecords);

    }


    @Test
    void test_create_consumer_group_by_offset(@Autowired RedisStreamService redisStreamService) {
        // 表示消费组从哪个消息偏移量开始读取消息.
        ReadOffset from = ReadOffset.from("1713764155222-0");
        String group = RandomUtil.randomString(4);

        // 创建群聊
        redisStreamService.createConsumerGroup(STREAM, group, from);

        // 读设置
        StreamReadOptions readOptions = StreamReadOptions.empty()
                // 不自动ACK
                .noack()
                .count(10);

        //表示从尾部开始读取.
        List<ObjectRecord<String, User>> recordList0 = redisStreamService.readGroupWithRecord(group, RandomUtil.randomString(4), User.class, readOptions,
                StreamOffset.create(STREAM,  ReadOffset.from(">")));
        System.out.println(recordList0);

        // 表示从尾部开始读取， 并且偏移量从指定的id = 1713764223917-0 开始, 包含（1713764223917-0）
        List<ObjectRecord<String, User>> recordList = redisStreamService.readGroupWithRecord(group, RandomUtil.randomString(4), User.class, readOptions,
                StreamOffset.create(STREAM, ReadOffset.from("1713764223917-0")),
                StreamOffset.create(STREAM,  ReadOffset.from(">")));
        System.out.println(recordList);

        // 不指定消费者组读取消息
        List<ObjectRecord<String, User>> objectRecords = redisStreamService.readWithRecord(User.class, readOptions, StreamOffset.fromStart(STREAM));
        System.out.println(objectRecords);

    }


    @Test
    void test_pending(@Autowired RedisStreamService redisStreamService) {
        List<String> strings = redisStreamService.pending(STREAM, GROUP, NAME);
        System.out.println(strings);

        System.out.println(redisStreamService.ack(STREAM, GROUP, strings.get(0)));

    }












    @Data
    @AllArgsConstructor
    public static class User {
        private String username;
        private String password;

    }

}
