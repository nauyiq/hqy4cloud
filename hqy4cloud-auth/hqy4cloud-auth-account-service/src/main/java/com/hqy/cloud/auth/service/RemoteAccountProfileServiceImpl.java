package com.hqy.cloud.auth.service;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hqy.cloud.account.service.RemoteAccountProfileService;
import com.hqy.cloud.account.struct.AccountProfileStruct;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheDelayRemoveService;
import com.hqy.cloud.auth.account.cache.AccountAuthCacheManager;
import com.hqy.cloud.auth.account.entity.AccountProfile;
import com.hqy.cloud.auth.account.service.AccountProfileService;
import com.hqy.cloud.auth.base.converter.AccountConverter;
import com.hqy.cloud.auth.base.dto.AccountInfoDTO;
import com.hqy.cloud.file.domain.AccountAvatarUtil;
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
    private final AccountProfileService accountProfileService;
    private final AccountOperationService accountOperationService;
    private final AccountAuthCacheDelayRemoveService accountAuthCacheDelayRemoveService;

    @Override
    public AccountProfileStruct getAccountProfile(Long userId) {
        AccountInfoDTO accountInfo = accountOperationService.getAccountInfo(userId);
        if (accountInfo == null) {
            return null;
        }
        return AccountConverter.CONVERTER.convertProfile(accountInfo);
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
        return AccountConverter.CONVERTER.convertProfile(accountInfo);
    }

    @Override
    public List<AccountProfileStruct> getAccountProfiles(List<Long> ids) {
        List<AccountInfoDTO> infos = accountOperationService.getAccountInfo(ids);
        if (CollectionUtils.isEmpty(infos)) {
            return Collections.emptyList();
        }
        return infos.stream().map(AccountConverter.CONVERTER::convertProfile).toList();
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
        return infos.stream().map(AccountConverter.CONVERTER::convertProfile).toList();
    }

    @Override
    public boolean uploadAccountProfile(AccountProfileStruct struct) {
        // 存储相对路径的头像.
        struct.avatar = AccountAvatarUtil.extractAvatar(struct.avatar);
        AccountProfile profile = new AccountProfile();
        AccountConverter.CONVERTER.update(profile, struct);
        // 第一次删除缓存
        AccountAuthCacheManager.getInstance().remove(struct.id);
        boolean result = accountProfileService.updateById(profile);
        if (result) {
            // 第二次删除缓存
            accountAuthCacheDelayRemoveService.removeAccountAuthCache(struct.id);
        }
        return result;
    }

    @Override
    public void updateAccountAvatar(Long id, String avatar) {
        avatar = AccountAvatarUtil.extractAvatar(avatar);
        if (StringUtils.isBlank(avatar)) {
            // 采用默认头像.
            avatar = AccountAvatarUtil.DEFAULT_AVATAR;
        }
        UpdateWrapper<AccountProfile> wrapper = Wrappers.update();
        wrapper.set("avatar", avatar);
        wrapper.eq("id", id);
        // 第一次删除缓存
        AccountAuthCacheManager.getInstance().remove(id);
        boolean result = accountProfileService.update(wrapper);
        if (!result) {
            log.warn("Failed execute to updateAccountAvatar, id: {}, avatar:{}.", id, avatar);
        }
        // 第二次删除缓存
        accountAuthCacheDelayRemoveService.removeAccountAuthCache(id);
    }

    @Override
    public boolean transactionalUploadAccountProfile(AccountProfileStruct profile) {
        if (profile == null || profile.id == null) {
            return false;
        }
        // 第一次删除缓存
        AccountAuthCacheManager.getInstance().remove(profile.id);
        AccountProfile accountProfile = accountProfileService.getById(profile.id);
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
        boolean result = accountProfileService.updateById(accountProfile);
        if (result) {
            // 第二次删除缓存
            accountAuthCacheDelayRemoveService.removeAccountAuthCache(profile.id);
        }
        return result;
    }

}
