package com.hqy.cloud.auth.account.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.account.entity.Account;
import com.hqy.cloud.auth.account.mapper.AccountMapper;
import com.hqy.cloud.auth.account.service.AccountService;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.util.AssertUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public Account queryAccountByUsernameOrEmail(String usernameOrEmail) {
        return mapper.queryAccountByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        return mapper.getAccountInfo(id);
    }

    @Override
    public AccountInfoDTO getAccountInfoByUsernameOrEmail(String usernameOrEmail) {
        return mapper.getAccountInfoByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    public List<AccountInfoDTO> getAccountInfos(List<Long> ids) {
        return mapper.getAccountInfos(ids);
    }

    @Override
    public List<AccountInfoDTO> getAccountInfosByName(String name) {
        return mapper.getAccountInfosByName(name);
    }

    @Override
    public PageResult<AccountInfoVO> getPageAccountInfos(String username, String nickname, Integer maxRoleLevel, Integer current, Integer size) {
        PageHelper.startPage(current, size);
        List<AccountInfoDTO> pageAccountInfos = mapper.getPageAccountInfos(username, nickname, maxRoleLevel);
        PageInfo<AccountInfoDTO> pageInfo = new PageInfo<>(pageAccountInfos);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(),
                pageInfo.getList().stream().map(AccountInfoVO::new).collect(Collectors.toList()));
    }
}
