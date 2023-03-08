package com.hqy.cloud.admin.service.impl;

import cn.hutool.core.convert.Convert;
import com.hqy.cloud.admin.service.RequestAdminSystemSettingService;
import com.hqy.cloud.auth.base.dto.BlackWhitelistDTO;
import com.hqy.cloud.auth.base.enums.WhiteListType;
import com.hqy.cloud.auth.limit.support.BiBlockedIpRedisService;
import com.hqy.cloud.auth.limit.support.ManualBlockedIpService;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.CommonResultCode;
import com.hqy.foundation.limit.service.ManualWhiteIpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/10 16:32
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RequestAdminSystemSettingServiceImpl implements RequestAdminSystemSettingService {

    private final ManualWhiteIpService manualWhiteIpService;
    private final BiBlockedIpRedisService biBlockedIpRedisService;
    private final ManualBlockedIpService manualBlockedIpService;

    @Override
    public R<Set<BlackWhitelistDTO>> queryWhitelist() {
        Set<BlackWhitelistDTO> whiteLists;
        Set<String> allWhiteIp = manualWhiteIpService.getAllWhiteIp();
        if (CollectionUtils.isEmpty(allWhiteIp)) {
            whiteLists = Collections.emptySet();
        } else {
            whiteLists = allWhiteIp.stream().map(value -> new BlackWhitelistDTO(WhiteListType.IP.name(), value)).collect(Collectors.toSet());
        }
        return R.ok(whiteLists);
    }

    @Override
    public R<Boolean> addWhitelist(BlackWhitelistDTO whiteListDTOBlack) {
        if (whiteListDTOBlack.getType().equalsIgnoreCase(WhiteListType.IP.name())) {
            manualWhiteIpService.addWhiteIp(whiteListDTOBlack.getValue());
        }
        return R.ok();
    }

    @Override
    public R<Boolean> deleteWhitelist(String type, String value) {
        if (type.equalsIgnoreCase(WhiteListType.IP.name())) {
            manualWhiteIpService.removeWhiteIp(value);
        }
        return R.ok();
    }

    @Override
    public R<Set<BlackWhitelistDTO>> queryBlacklist() {
        Set<BlackWhitelistDTO> blackLists = new HashSet<>();
        Map<String, Long> biBlockedIpMap = biBlockedIpRedisService.getAllBlockIp();
        if (MapUtils.isNotEmpty(biBlockedIpMap)) {
            blackLists.addAll(biBlockedIpMap.entrySet().stream()
                    .map(entry -> new BlackWhitelistDTO(BiBlockedIpRedisService.NAME, entry.getKey(), entry.getValue())).collect(Collectors.toSet()));
        }
        Map<String, Long> manualBlockedIpMap = manualBlockedIpService.getAllBlockIp();
        if (MapUtils.isNotEmpty(manualBlockedIpMap)) {
            blackLists.addAll(manualBlockedIpMap.entrySet().stream()
                    .map(entry -> new BlackWhitelistDTO(ManualBlockedIpService.NAME, entry.getKey(), entry.getValue())).collect(Collectors.toSet()));
        }
        return R.ok(blackLists);
    }

    @Override
    public R<Boolean> addBlacklist(BlackWhitelistDTO blackWhitelistDTO) {
        Long expired = blackWhitelistDTO.getExpired();
        if (Objects.isNull(expired)) {
            manualBlockedIpService.addBlockIp(blackWhitelistDTO.getValue(),  Integer.MAX_VALUE);
        } else {
            manualBlockedIpService.addBlockIp(blackWhitelistDTO.getValue(), Convert.toInt(expired / 1000));
        }
        return R.ok();
    }

    @Override
    public R<Boolean> deleteBlacklist(String type, String value) {
        if (type.equalsIgnoreCase(BiBlockedIpRedisService.NAME)) {
            biBlockedIpRedisService.removeBlockIp(value);
        }
        if (type.equalsIgnoreCase(ManualBlockedIpService.NAME)) {
            manualBlockedIpService.removeBlockIp(value);
        }
        return R.ok();
    }
}
