package com.hqy.cloud.auth.account.service.impl;

import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.mapper.AccountMapper;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
@RequiredArgsConstructor
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {
    private final AccountMapper mapper;

    @Override
    public Long getAccountIdByUsernameOrEmail(String value) {
        return mapper.getAccountIdByUsernameOrEmail(value);
    }

    @Override
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
    @Cached(name = AccountAuthCacheManager.ACCOUNT_USER_CACHE_KEY, expire = 1440,  cacheType = CacheType.BOTH, key = "#id", cacheNullValue = true)
    public Account findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public Account queryAccountByUniqueIndex(String uniqueIndex) {
        return mapper.queryAccountByUniqueIndex(uniqueIndex);
    }

    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        return mapper.getAccountInfo(id);
    }

    @Override
    public AccountInfoDTO getAccountInfo(String phoneOrEmail) {
        return mapper.getAccountInfoByPhoneOrEmail(phoneOrEmail);
    }

    @Override
    public AccountInfoDTO getAccountInfoByUsernameOrEmail(String usernameOrEmail) {
        return mapper.getAccountInfoByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    public List<AccountInfoDTO> getAccountInfos(List<Long> ids) {
        /*Map<Long, AccountInfoDTO> map = AccountAuthCacheManager.getInstance().getAccountCache()
                .getAll(new HashSet<>(ids));
        List<AccountInfoDTO> result = new ArrayList<>(map.values());
        if (MapUtils.isEmpty(map) || map.size() != ids.size()) {
            List<Long> findIds = ids.stream().filter((id -> !map.containsKey(id))).toList();
            List<AccountInfoDTO> accountInfos = mapper.getAccountInfos(findIds);
            if (CollectionUtils.isNotEmpty(accountInfos)) {
                Map<Long, AccountInfoDTO> findMap = accountInfos.stream().collect(Collectors.toMap(AccountInfoDTO::getId, Function.identity()));
                AccountAuthCacheManager.getInstance().getAccountCache().putAll(findMap);
                result.addAll(accountInfos);
            }
        }
        return result;*/
        return null;
    }

    @Override
    public List<AccountInfoDTO> getAccountInfosByName(String name) {
        return mapper.getAccountProfilesByName(name);
    }

    @Override
    public PageResult<AccountInfoVO> getPageAccountInfos(String username, String nickname, Integer current, Integer size) {
        PageHelper.startPage(current, size);
        List<AccountInfoDTO> pageAccountInfos = mapper.getPageAccountInfos(username, nickname);
        PageInfo<AccountInfoDTO> pageInfo = new PageInfo<>(pageAccountInfos);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(),
                pageInfo.getList().stream().map(AccountInfoVO::new).collect(Collectors.toList()));
    }
}
