package com.hqy.cloud.common.pipeline;

import com.hqy.cloud.common.base.exception.BizException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 管道， 用于责任链执行
 * @author hongqy
 * @date 2025/10/14
 */
@Slf4j
public class Pipeline<T> {
    private final List<PipelineHandler<T>> pipeline;
    private final Config config;

    public Pipeline() {
        this(new ArrayList<>(), Config.defaultConfig());
    }

    public Pipeline(List<PipelineHandler<T>> handlers) {
       this(handlers, Config.defaultConfig());
    }

    public Pipeline(Config config) {
        this(new ArrayList<>(), config);
    }

    public Pipeline(List<PipelineHandler<T>> handlers, Config config) {
        this.pipeline = handlers;
        this.config = config;
    }

    public Pipeline<T> addNext(PipelineHandler<T> handler) {
        Assert.notNull(handler, "handler can not be null");
        this.pipeline.add(handler);
        return this;
    }


    public void execute(T context) {
        StopWatch started = StopWatch.createStarted();
        log.info("管道执行开始.");

        for (PipelineHandler<T> handler : pipeline) {
            try {
                handler.handle(context);
            } catch (Exception ex) {
                log.error("管道执行出错, cause:{}", ex.getMessage(), ex);
                if (!this.config.catchException) {
                    log.warn("发生异常, 管道执行中断, 不再执行后续流程.");
                    throw ex;
                }
                if (this.config.breakOnError) {
                    log.warn("发生异常, 管道执行中断, 不再执行后续流程.");
                    break;
                }
            }

            // 获取中断标识
            Boolean breakFlag = PipelineConfigHolder.getAndRemoveBreakFlag();
            if (breakFlag) {
                log.info("检测到管道中断标识, 当前管道执行中断, 不再执行后续流程.");
                break;
            }
        }

        log.info("管道执行结束, cost ms:{}", started.getTime());
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {

        /**
         * 执行异常时是否中断管道
         */
        private boolean breakOnError;

        /**
         * 是否捕获异常
         */
        private boolean catchException;

        public static Config defaultConfig() {
            return new Config(true, true);
        }


    }





}
