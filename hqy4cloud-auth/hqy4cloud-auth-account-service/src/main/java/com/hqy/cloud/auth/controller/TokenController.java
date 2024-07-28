package com.hqy.cloud.auth.controller;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.auth.utils.AuthUtils;
import com.hqy.cloud.cache.common.RedisConstants;
import com.hqy.cloud.cache.redis.server.support.SmartRedisManager;
import com.hqy.cloud.common.bind.R;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * 生成唯一token， 用于需要控制幂等的场景
 * @author qiyuan.hong
 * @date 2024/7/18
 */
@Slf4j
@RequestMapping("token")
@RestController
@RequiredArgsConstructor
public class TokenController {

    private static final String TOKEN_PREFIX = "token:";

    @GetMapping("/get")
    public R<String> get(@NotBlank String scene) {
        Long currentUserId = AuthUtils.getCurrentUserId();
        if (log.isDebugEnabled()) {
            log.debug("User get token by scene: {}, user id: {}", scene, currentUserId);
        }
        // 用uuid生成唯一token
        String token = UUID.randomUUID().toString();
        String key = TOKEN_PREFIX + scene + RedisConstants.CACHE_KEY_SEPARATOR + token;
        Boolean result = SmartRedisManager.getInstance().set(key, currentUserId, 30L, TimeUnit.MINUTES);
        return result ? R.ok(key) : R.failed();

    }




}
