package com.hqy.cloud.auth.service.tk.support;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hqy.account.dto.AccountInfoDTO;
import com.hqy.cloud.auth.mapper.AccountTkMapper;
import com.hqy.cloud.common.result.PageResult;
import com.hqy.cloud.auth.base.vo.AccountInfoVO;
import com.hqy.cloud.auth.entity.Account;
import com.hqy.cloud.auth.service.tk.AccountTkService;
import com.hqy.cloud.db.tk.BaseTkMapper;
import com.hqy.cloud.db.tk.support.BaseTkServiceImpl;
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
public class AccountTkServiceImpl extends BaseTkServiceImpl<Account, Long> implements AccountTkService {

    private final AccountTkMapper accountDao;

    @Override
    public BaseTkMapper<Account, Long> getTkMapper() {
        return accountDao;
    }


    @Override
    public Account queryAccountByUsernameOrEmail(String usernameOrEmail) {
        return accountDao.queryAccountByUsernameOrEmail(usernameOrEmail);
    }

    @Override
    public AccountInfoDTO getAccountInfo(Long id) {
        AssertUtil.notNull(id, "Account id should not be null.");
        return accountDao.getAccountInfo(id);
    }

    @Override
    public List<AccountInfoDTO> getAccountInfos(List<Long> ids) {
        return accountDao.getAccountInfos(ids);
    }

    @Override
    public PageResult<AccountInfoVO> getPageAccountInfos(String username, String nickname, Integer maxRoleLevel, Integer current, Integer size) {
        PageHelper.startPage(current, size);
        List<AccountInfoDTO> pageAccountInfos = accountDao.getPageAccountInfos(username, nickname, maxRoleLevel);
        PageInfo<AccountInfoDTO> pageInfo = new PageInfo<>(pageAccountInfos);
        return new PageResult<>(pageInfo.getPageNum(), pageInfo.getTotal(), pageInfo.getPages(),
                pageInfo.getList().stream().map(AccountInfoVO::new).collect(Collectors.toList()));
    }
}
