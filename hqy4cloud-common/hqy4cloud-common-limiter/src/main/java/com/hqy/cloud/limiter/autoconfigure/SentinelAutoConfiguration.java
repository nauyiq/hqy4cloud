package com.hqy.cloud.limiter.autoconfigure;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.util.JsonUtil;
import com.hqy.cloud.util.config.YamlPropertySourceFactory;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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
@Slf4j
@Configuration
@RequiredArgsConstructor
@PropertySource(value = "classpath:sentinel-config.yml", factory = YamlPropertySourceFactory.class)
public class SentinelAutoConfiguration {

    @Bean
    public BlockExceptionHandler blockExceptionHandler() {
        return (httpServletRequest, httpServletResponse, e) -> {
            if (e instanceof DegradeException) {
                out(httpServletResponse, HttpStatus.FORBIDDEN.value(), R.failed(ResultCode.INTERFACE_ALREADY_DEGRADE));
            } else if (e instanceof ParamFlowException) {
                out(httpServletResponse, HttpStatus.FORBIDDEN.value(), R.failed(ResultCode.INTERFACE_PRAM_HOT_KET_LIMIT));
            } else if (e instanceof AuthorityException) {
                out(httpServletResponse, HttpStatus.FORBIDDEN.value(), R.failed(ResultCode.SENTINEL_LIMITED_AUTHORITY));
            } else {
                out(httpServletResponse, HttpStatus.FORBIDDEN.value(), R.failed(ResultCode.INTERFACE_LIMITED));
            }
        };
    }


    private <T> void out(HttpServletResponse response, int statusCode, R<T> result) {
        try {
            if (Objects.isNull(response)) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (Objects.nonNull(servletRequestAttributes)) {
                    response = servletRequestAttributes.getResponse();
                }
            }

            if (Objects.nonNull(response) && !response.isCommitted()) {
                //是否乱码
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType("application/json");
                response.setStatus(statusCode);
                PrintWriter writer = response.getWriter();
                writer.write(JsonUtil.toJson(result));
                writer.flush();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

}
