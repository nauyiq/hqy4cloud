package com.hqy.cloud.foundation.cache;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.registry.context.ProjectContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * 默认的缓存key生成器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/28 10:28
 */
@Slf4j
public class IKeyGenerator implements KeyGenerator {
    private IKeyGenerator() {
    }
    private final static IKeyGenerator INSTANCE  = new IKeyGenerator();
    public static IKeyGenerator getInstance() {
        return INSTANCE;
    }

    @Override
    public Object generate(Object target, Method method, Object... params) {
        // 环境
        String env = ProjectContext.getContextInfo().getEnv();
        // 分类
        String simpleName = target.getClass().getSimpleName();
        StringBuilder sb = new StringBuilder();
        sb.append(env)
                .append(StrUtil.COLON)
                .append(simpleName)
                .append(StrUtil.COLON);
        for (Object param : params) {
            sb.append(param.toString());
        }
        return sb.toString();
    }
}
