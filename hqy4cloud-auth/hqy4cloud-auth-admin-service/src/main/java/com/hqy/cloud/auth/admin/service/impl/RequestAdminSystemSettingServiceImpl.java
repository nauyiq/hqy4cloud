package com.hqy.cloud.auth.admin.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.hqy.cloud.auth.admin.service.RequestAdminSystemSettingService;
import com.hqy.cloud.auth.base.dto.BlackWhitelistDTO;
import com.hqy.cloud.auth.base.enums.WhiteListType;
import com.hqy.cloud.auth.base.vo.BlackAddressVO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.limit.api.ManualWhiteIpService;
import com.hqy.cloud.limit.core.BiBlockedIpRedisService;
import com.hqy.cloud.limit.core.BlockDTO;
import com.hqy.cloud.limit.core.ManualBlockedIpService;
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
            manualWhiteIpService.addWhiteIp(whiteListDTOBlack.getIp());
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
    public R<Set<BlackAddressVO>> queryBlacklist() {
        Set<BlackAddressVO> blackLists = new HashSet<>();
        Map<String, BlockDTO> biBlockedIpMap = biBlockedIpRedisService.getAllBlocked();
        if (MapUtils.isNotEmpty(biBlockedIpMap)) {
            blackLists.addAll(biBlockedIpMap.entrySet().stream()
                    .map(entry -> {
                        BlockDTO value = entry.getValue();
                        return new BlackAddressVO(BiBlockedIpRedisService.NAME, entry.getKey(),
                                value.getBlockedMillis() / 1000, DateUtil.formatDateTime(new Date(value.getBlockedTimestamp())));
                    }).collect(Collectors.toSet()));
        }
        Map<String, BlockDTO> manualBlockedIpMap = manualBlockedIpService.getAllBlocked();
        if (MapUtils.isNotEmpty(manualBlockedIpMap)) {
            blackLists.addAll(manualBlockedIpMap.entrySet().stream()
                    .map(entry -> {
                        BlockDTO value = entry.getValue();
                        return new BlackAddressVO(ManualBlockedIpService.NAME, entry.getKey(),
                                value.getBlockedMillis() / 1000, DateUtil.formatDateTime(new Date(value.getBlockedTimestamp())));
                    }).collect(Collectors.toSet()));
        }
        return R.ok(blackLists);
    }

    @Override
    public R<Boolean> addBlacklist(BlackWhitelistDTO blackWhitelistDTO) {
        Long expired = blackWhitelistDTO.getExpired();
        if (Objects.isNull(expired)) {
            manualBlockedIpService.addBlockIp(blackWhitelistDTO.getIp(),  Integer.MAX_VALUE);
        } else {
            manualBlockedIpService.addBlockIp(blackWhitelistDTO.getIp(), Convert.toInt(expired / 1000));
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
