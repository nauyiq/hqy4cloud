package com.hqy.cloud.limiter.flow;

import com.hqy.cloud.common.swticher.ServerSwitcher;
import com.hqy.cloud.limiter.api.AbstractLimiter;
import com.hqy.cloud.limiter.core.GuavaCacheTokenBucketLimiter;
import com.hqy.cloud.limiter.core.RedisResourceSlidingWindowsLimiter;
import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

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
        if (ServerSwitcher.ENABLE_SHARE_IP_OVER_REQUEST_STATISTICS.isOff()) {
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
                return FlowResult.buildBlock(limiter.getConfig().getBlockSeconds());
            }
        } catch (Exception e) {
            //如果有异常，catch住，当做未超限来处理.
            log.error("流量控制组件:执行计数操作失败,无法执行计数", e);
        }
        return FlowResult.build();
    }


}
