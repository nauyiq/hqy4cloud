package com.hqy.cloud.db.autoconfigure;

import com.hqy.cloud.db.mapper.CommonMapper;
import com.hqy.cloud.db.service.CommonDbService;
import com.hqy.cloud.db.service.impl.CommonDbServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/2/4
 */
@Configuration
public class DatabaseConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CommonDbService commonDbService(CommonMapper commonMapper) {
        return new CommonDbServiceImpl(commonMapper);
    }

}
