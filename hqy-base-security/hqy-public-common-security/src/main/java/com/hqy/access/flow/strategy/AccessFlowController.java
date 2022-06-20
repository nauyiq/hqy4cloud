package com.hqy.access.flow.strategy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.access.flow.FlowLimitConfig;
import com.hqy.access.flow.FlowResult;
import com.hqy.base.common.swticher.HttpGeneralSwitcher;
import com.hqy.util.AssertUtil;
import com.hqy.util.ReflectUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的流量控制器
 * redis基于spring data redis的redisTemplate
 * @author qy
 * @date 2021-08-04 16:43
 */
@Slf4j
public class AccessFlowController {

    private static final String KEY_PREFIX_STRING = ReflectUtil.genkeyPrefix(AccessFlowController.class);

    private final AbstractLimiter localCacheLimiter;

    private final AbstractLimiter redisCacheLimiter;

    private final Cache<String, String> overLimitCache =
            CacheBuilder.newBuilder().initialCapacity(1024).expireAfterWrite(10L, TimeUnit.MINUTES).build();

    public AccessFlowController(FlowLimitConfig config) {
        AssertUtil.notNull(config, "Initialize RedisFlowController failure.");
        redisCacheLimiter = new RedisResourceSlidingWindowsLimiter(config);
        localCacheLimiter = new GuavaCacheTokenBucketLimiter(config);
    }

    private String genKey(String resource) {
        AssertUtil.notEmpty(resource, "Limit resource is empty, please check param.");
        if (!resource.startsWith(KEY_PREFIX_STRING)) {
            return KEY_PREFIX_STRING.concat(resource);
        }
        return resource;
    }

    public AbstractLimiter getLimiter() {
        if (HttpGeneralSwitcher.ENABLE_SHARE_IP_OVER_REQUEST_STATISTICS.isOff()) {
            return localCacheLimiter;
        } else {
            return redisCacheLimiter;
        }
    }

    /**
     * 是否访问超限?
     * @param resource    自定义一个表示资源访问限次的串
     * @return FlowResult 流量访问接口
     */
    public FlowResult isOverLimit(String resource) {
        resource = genKey(resource);
        boolean limiting = false;
        try {
            limiting = getLimiter().isOverLimit(resource);
        } catch (Exception e) {
            //如果redis 有异常，catch住，当做未超限来处理.
            log.error("流量控制组件:执行计数操作失败,无法执行计数", e);
        }
        //判断是否大于阈值, 超过返回false
        if (limiting) {
            //如果首次超限 首次时间内放行 不封禁ip
            String data = overLimitCache.getIfPresent(resource);
            if (StringUtils.isNotBlank(data)) {
                return new FlowResult(true, true);
            }
            overLimitCache.put(resource, resource);
            return new FlowResult(true, false);
        }
        return new FlowResult(false, false);
    }


}
