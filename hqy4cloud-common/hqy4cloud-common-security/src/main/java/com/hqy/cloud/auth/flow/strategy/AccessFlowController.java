package com.hqy.cloud.auth.flow.strategy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.hqy.cloud.auth.flow.FlowLimitConfig;
import com.hqy.cloud.auth.flow.FlowResult;
import com.hqy.cloud.common.swticher.HttpGeneralSwitcher;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * 流量访问控制器
 * @author qy
 * @date 2021-08-04 16:43
 */
@Slf4j
public class AccessFlowController {

    private static final String KEY_PREFIX_STRING = ReflectUtils.genkeyPrefix(AccessFlowController.class);

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
        try {
            AbstractLimiter limiter = getLimiter();
            if (limiter.isOverLimit(resource)) {
                //如果首次超限 首次时间内放行 不封禁ip
                String data = overLimitCache.getIfPresent(resource);
                if (StringUtils.isNotBlank(data)) {
                    return FlowResult.buildBlock(limiter.getConfig().getBlockSeconds());
                }
                overLimitCache.put(resource, resource);
                return FlowResult.buildLimit();
            }
        } catch (Exception e) {
            //如果有异常，catch住，当做未超限来处理.
            log.error("流量控制组件:执行计数操作失败,无法执行计数", e);
        }
        return FlowResult.build();
    }


}
