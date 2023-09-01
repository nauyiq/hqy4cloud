package com.hqy.cloud.auth.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.account.service.RemoteAccountProfileService;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.auth.entity.AccountProfile;
import com.hqy.cloud.auth.service.impl.AccountCacheService;
import com.hqy.cloud.auth.service.tk.AccountProfileTkService;
import com.hqy.cloud.rpc.thrift.service.AbstractRPCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/9/29 18:12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteAccountProfileServiceImpl extends AbstractRPCService implements RemoteAccountProfileService {

    private final AccountOperationService accountOperationService;
    private final AccountCacheService baseInfoCacheService;

    @Override
    public boolean uploadAccountProfile(AccountProfileStruct profileStruct) {
        Long id = profileStruct.id;
        AccountProfileTkService accountProfileTkService = accountOperationService.getAccountProfileTkService();
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
