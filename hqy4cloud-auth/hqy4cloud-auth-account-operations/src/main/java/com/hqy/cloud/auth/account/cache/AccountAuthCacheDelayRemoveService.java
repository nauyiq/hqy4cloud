package com.hqy.cloud.auth.account.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 用户缓存延迟删除服务
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/27
 */
@Slf4j
@Service
public class AccountAuthCacheDelayRemoveService {

    @Scheduled(fixedDelay = 2, timeUnit = TimeUnit.SECONDS)
    public void removeAccountAuthCache(Long accountId) {
        boolean remove = AccountAuthCacheManager.getInstance().getAccountCache().remove(accountId);
        log.info("Remove account cache by id: {}, result:{}", accountId, remove);
    }

}
