package com.hqy.cloud.sentinel.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.util.ResponseUtil;
import com.hqy.cloud.util.config.YamlPropertySourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * sentinel流控规则持久化字段详解：
 * resource：资源名称
 * limitApp：来源应用
 * grade：阀值类型，0：线程数，1：QPS
 * count：单机阀值
 * strategy：流控模式，0：直接，1：关联，2：链路
 * controlBehavior：流控效果，0：快速失败，1：warmUp，2：排队等待
 * clusterMode：是否集群
 *
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/4/24 17:23
 */
@Configuration
@PropertySource(value = "classpath:sentinel-config.yml", factory = YamlPropertySourceFactory.class)
public class SentinelAutoConfiguration {

    @Bean
    public BlockExceptionHandler blockExceptionHandler() {
        return (httpServletRequest, httpServletResponse, e) ->
                ResponseUtil.out(httpServletResponse, HttpStatus.FORBIDDEN.value(), R.failed(ResultCode.INTERFACE_LIMITED));
    }



}
