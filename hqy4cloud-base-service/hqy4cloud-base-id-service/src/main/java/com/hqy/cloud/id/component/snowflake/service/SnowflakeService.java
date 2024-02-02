package com.hqy.cloud.id.component.snowflake.service;

import com.hqy.cloud.id.exception.InitException;
import com.hqy.cloud.id.service.IdGen;
import com.hqy.cloud.id.service.IdGenService;
import com.hqy.cloud.id.struct.ResultStruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * SnowflakeService.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/22 15:06
 */
@Slf4j
@Service
public class SnowflakeService implements IdGenService {


    private final IdGen idGen;


    @SneakyThrows
    public SnowflakeService(RedissonClient redissonClient) {
         this.idGen = new SnowflakeIdGen(redissonClient);
        if (idGen.init()) {
            log.info("Snowflake service init successfully.");
        } else {
            throw new InitException("Snowflake service init failed.");
        }
    }

    @Override
    public ResultStruct get(String key) {
        return idGen.get(key);
    }

}
