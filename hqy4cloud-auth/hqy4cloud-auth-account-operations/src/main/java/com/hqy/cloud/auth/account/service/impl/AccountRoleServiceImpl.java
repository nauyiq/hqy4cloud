package com.hqy.cloud.auth.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hqy.cloud.auth.account.entity.AccountRole;
import com.hqy.cloud.auth.account.mapper.AccountRoleMapper;
import com.hqy.cloud.auth.account.service.AccountRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/12 16:30
 */
@Service
@RequiredArgsConstructor
public class AccountRoleServiceImpl extends ServiceImpl<AccountRoleMapper, AccountRole> implements AccountRoleService {
    private final AccountRoleMapper mapper;

    @Override
    public boolean updateRoleLevel(Integer roleId, Integer level) {
        return mapper.updateRoleLevel(roleId, level) > 0;
    }

    @Override
    public boolean deleteByAccountRoleIds(List<Integer> roleIds) {
        QueryWrapper<AccountRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_id", roleIds);
        mapper.delete(queryWrapper);
        return true;
    }
}
