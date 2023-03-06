package com.hqy.cloud.admin.service.impl;

import cn.hutool.core.convert.Convert;
import com.hqy.cloud.auth.limit.support.BiBlockedIpRedisService;
import com.hqy.cloud.auth.limit.support.ManualBlockedIpService;
import com.hqy.cloud.admin.service.RequestAdminSystemSettingService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.auth.base.dto.BlackWhitelistDTO;
import com.hqy.cloud.auth.base.enums.WhiteListType;
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
    public DataResponse queryWhitelist() {
        Set<BlackWhitelistDTO> whiteLists;
        Set<String> allWhiteIp = manualWhiteIpService.getAllWhiteIp();
        if (CollectionUtils.isEmpty(allWhiteIp)) {
            whiteLists = Collections.emptySet();
        } else {
            whiteLists = allWhiteIp.stream().map(value -> new BlackWhitelistDTO(WhiteListType.IP.name(), value)).collect(Collectors.toSet());
        }
        return CommonResultCode.dataResponse(whiteLists);
    }

    @Override
    public MessageResponse addWhitelist(BlackWhitelistDTO whiteListDTOBlack) {
        if (whiteListDTOBlack.getType().equalsIgnoreCase(WhiteListType.IP.name())) {
            manualWhiteIpService.addWhiteIp(whiteListDTOBlack.getValue());
        }
        return CommonResultCode.messageResponse();
    }

    @Override
    public MessageResponse deleteWhitelist(String type, String value) {
        if (type.equalsIgnoreCase(WhiteListType.IP.name())) {
            manualWhiteIpService.removeWhiteIp(value);
        }
        return CommonResultCode.messageResponse();
    }

    @Override
    public DataResponse queryBlacklist() {
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
        return CommonResultCode.dataResponse(blackLists);
    }

    @Override
    public MessageResponse addBlacklist(BlackWhitelistDTO blackWhitelistDTO) {
        Long expired = blackWhitelistDTO.getExpired();
        if (Objects.isNull(expired)) {
            manualBlockedIpService.addBlockIp(blackWhitelistDTO.getValue(),  Integer.MAX_VALUE);
        } else {
            manualBlockedIpService.addBlockIp(blackWhitelistDTO.getValue(), Convert.toInt(expired / 1000));
        }
        return CommonResultCode.messageResponse();
    }

    @Override
    public MessageResponse deleteBlacklist(String type, String value) {
        if (type.equalsIgnoreCase(BiBlockedIpRedisService.NAME)) {
            biBlockedIpRedisService.removeBlockIp(value);
        }
        if (type.equalsIgnoreCase(ManualBlockedIpService.NAME)) {
            manualBlockedIpService.removeBlockIp(value);
        }
        return CommonResultCode.messageResponse();
    }
}
