package com.hqy.cloud.auth.account.service.impl;

import com.alicp.jetcache.anno.CacheRefresh;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.base.vo.AccountRoleVO;
import com.hqy.cloud.auth.account.entity.Role;
import com.hqy.cloud.auth.account.mapper.RoleMapper;
import com.hqy.cloud.auth.account.service.RoleService;
import com.hqy.cloud.common.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2022-03-10 21:18
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    private final RoleMapper mapper;

    @Override
    @CacheRefresh(refresh = 60, timeUnit = TimeUnit.MINUTES)
    @Cached(name = AccountAuthCacheManager.ACCOUNT_ROLE_CACHE_KEY, expire = 3000, cacheType = CacheType.BOTH, key = "#id", cacheNullValue = true)
    public Role findById(Integer id) {
        return mapper.findById(id);
    }

    @Override
    public List<Integer> selectIdByNames(List<String> roleList) {
        if (CollectionUtils.isEmpty(roleList)) {
            return null;
        }
        return mapper.selectIdByNames(roleList);
    }

    @Override
    public List<Role> queryRolesByNames(List<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        return mapper.queryRolesByNames(roles);
    }

    @Override
    public List<Role> getRolesList(Integer maxRoleLevel, Boolean status) {
        return mapper.queryRoles(maxRoleLevel, status);
    }

    @Override
    public PageResult<AccountRoleVO> getPageRoles(String roleName, String note, Integer maxRoleLevel, Integer current, Integer size) {
        PageHelper.startPage(current, size);
        List<AccountRoleVO> accountRoleVOS = mapper.getPageRoleVo(roleName, note, maxRoleLevel);
        if (CollectionUtils.isEmpty(accountRoleVOS)) {
            return new PageResult<>();
        }
        PageInfo<AccountRoleVO> pageInfo = new PageInfo<>(accountRoleVOS);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(), pageInfo.getList());
    }
}
