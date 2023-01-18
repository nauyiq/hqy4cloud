package com.hqy.account.service.impl.remote;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hqy.auth.entity.AccountProfile;
import com.hqy.auth.service.AccountAuthService;
import com.hqy.auth.service.AccountProfileTkService;
import com.hqy.auth.common.cache.AccountBaseInfoCacheService;
import com.hqy.account.service.remote.AccountProfileRemoteService;
import com.hqy.account.struct.AccountProfileStruct;
import com.hqy.rpc.thrift.service.AbstractRPCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 18:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountProfileRemoteServiceImpl extends AbstractRPCService implements AccountProfileRemoteService {

    private final AccountAuthService accountAuthService;
    private final AccountBaseInfoCacheService baseInfoCacheService;


    @Override
    public List<AccountProfileStruct> getAccountProfiles(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        //TODO.

        return null;
    }

    @Override
    public boolean uploadAccountProfile(AccountProfileStruct profileStruct) {
        Long id = profileStruct.id;
        AccountProfileTkService accountProfileTkService = accountAuthService.getAccountProfileTkService();
        AccountProfile accountProfile = accountProfileTkService.queryById(id);
        if (accountProfile == null) {
            return false;
        }

        //setting information.
        accountProfile.setIntro(profileStruct.intro);
        if (StringUtils.isNotBlank(profileStruct.birthday)) {
            DateTime dateTime = DateUtil.parseDateTime(profileStruct.birthday);
            accountProfile.setBirthday(dateTime);
        }
        if (StringUtils.isNotBlank(profileStruct.nickname)) {
            accountProfile.setNickname(profileStruct.nickname);
        }

        if (StringUtils.isNotBlank(profileStruct.avatar)) {
            accountProfile.setAvatar(profileStruct.avatar);
        }

        boolean update = accountProfileTkService.update(accountProfile);
        if (update) {
            baseInfoCacheService.invalid(id);
        }
        return update;
    }
}
