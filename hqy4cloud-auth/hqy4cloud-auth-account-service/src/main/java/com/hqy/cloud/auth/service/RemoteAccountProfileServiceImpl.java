package com.hqy.cloud.auth.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.account.dto.AccountInfoDTO;
import com.hqy.cloud.account.service.RemoteAccountProfileService;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.auth.base.converter.AccountConverter;
import com.hqy.cloud.auth.base.dto.AccountDTO;
import com.hqy.cloud.auth.cache.support.AccountCacheService;
import com.hqy.cloud.auth.entity.AccountProfile;
import com.hqy.cloud.auth.service.tk.AccountProfileTkService;
import com.hqy.cloud.foundation.common.account.AccountAvatarUtil;
import com.hqy.cloud.rpc.thrift.service.AbstractRPCService;
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
public class RemoteAccountProfileServiceImpl extends AbstractRPCService implements RemoteAccountProfileService {
    private final AccountProfileTkService accountProfileTkService;
    private final AccountOperationService accountOperationService;
    private final AccountCacheService accountCacheService;

    @Override
    public AccountProfileStruct getAccountProfile(Long userId) {
        AccountDTO account = accountCacheService.getData(userId);
        AccountProfile profile = accountProfileTkService.queryById(userId);
        if (account == null || profile == null) {
            return new AccountProfileStruct();
        }
        // return host avatar.
        String avatar = AccountAvatarUtil.getAvatar(profile.getAvatar());
        profile.setAvatar(avatar);
        AccountProfileStruct struct = AccountConverter.CONVERTER.convert(profile);
        struct.username = account.getUsername();
        return struct;
    }

    @Override
    public AccountProfileStruct getAccountProfileByUsernameOrEmail(String usernameOrEmail) {
        AccountInfoDTO accountInfo = accountOperationService.getAccountInfo(usernameOrEmail);
        if (accountInfo == null) {
            if (log.isDebugEnabled()) {
                log.debug("Not found account info by usernameOrEmail:{}.", usernameOrEmail);
            }
            return new AccountProfileStruct();
        }
        return buildAccountProfileStruct(accountInfo);
    }

    @Override
    public List<AccountProfileStruct> getAccountProfiles(List<Long> ids) {
        List<AccountInfoDTO> infos = accountOperationService.getAccountInfo(ids);
        if (CollectionUtils.isEmpty(infos)) {
            return Collections.emptyList();
        }
        return infos.stream().map(this::buildAccountProfileStruct).toList();
    }

    @Override
    public List<AccountProfileStruct> getAccountProfilesByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return Collections.emptyList();
        }
        List<AccountInfoDTO> infos = accountOperationService.getAccountProfilesByName(name);
        if (CollectionUtils.isEmpty(infos)) {
            return Collections.emptyList();
        }
        return infos.stream().map(this::buildAccountProfileStruct).toList();
    }

    @Override
    public boolean uploadAccountProfile(AccountProfileStruct struct) {
        // 存储相对路径的头像.
        struct.avatar = AccountAvatarUtil.extractAvatar(struct.avatar);
        AccountProfile profile = new AccountProfile();
        AccountConverter.CONVERTER.update(profile, struct);
        return accountProfileTkService.updateSelective(profile);

    }

    @Override
    public void updateAccountAvatar(Long id, String avatar) {
        avatar = AccountAvatarUtil.extractAvatar(avatar);
        if (StringUtils.isBlank(avatar)) {
            // 采用默认头像.
            avatar = AccountAvatarUtil.DEFAULT_AVATAR;
        }
        AccountProfile profile = new AccountProfile(id, null, avatar);
        boolean result = accountProfileTkService.updateSelective(profile);
        if (!result) {
            log.warn("Failed execute to updateAccountAvatar, id: {}, avatar:{}.", id, avatar);
        }
    }

    @Override
    public boolean transactionalUploadAccountProfile(AccountProfileStruct profile) {
        if (profile == null || profile.id == null) {
            return false;
        }
        AccountProfile accountProfile = accountProfileTkService.queryById(profile.id);
        if (accountProfile == null) {
            return false;
        }
        // 设置参数
        if (StringUtils.isNotBlank(profile.nickname)) {
            accountProfile.setNickname(profile.nickname);
        }
        if (StringUtils.isNotBlank(profile.avatar)) {
            accountProfile.setAvatar(AccountAvatarUtil.extractAvatar(profile.avatar));
        }
        if (StringUtils.isNotBlank(profile.birthday)) {
            accountProfile.setBirthday(DateUtil.parseDateTime(profile.birthday));
        }
        if (profile.sex != null) {
            accountProfile.setSex(profile.sex);
        }
        accountProfile.setIntro(profile.intro);
        return accountProfileTkService.update(accountProfile);
    }

    private AccountProfileStruct buildAccountProfileStruct(AccountInfoDTO accountInfo) {
        return AccountProfileStruct.builder()
                .id(accountInfo.getId())
                .username(accountInfo.getUsername())
                .nickname(accountInfo.getNickname())
                .avatar(AccountAvatarUtil.getAvatar(accountInfo.getAvatar()))
                .intro(accountInfo.getIntro())
                .birthday(DateUtil.format(accountInfo.getBirthday(), DatePattern.NORM_DATETIME_MINUTE_PATTERN))
                .sex(accountInfo.getSex()).build();
    }
}
