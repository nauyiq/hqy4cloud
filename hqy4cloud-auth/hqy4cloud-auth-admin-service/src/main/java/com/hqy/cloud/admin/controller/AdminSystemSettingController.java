package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminSystemSettingService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.auth.base.dto.BlackWhitelistDTO;
import com.hqy.cloud.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/10 16:22
 */
@Slf4j
@RestController
@RequestMapping("/admin/system")
@RequiredArgsConstructor
public class AdminSystemSettingController {

    private final RequestAdminSystemSettingService requestService;

    @GetMapping("/whitelist/all")
    public DataResponse queryWhiteList() {
        return requestService.queryWhitelist();
    }

    @PostMapping("/whitelist")
    public MessageResponse addWhiteList(@RequestBody @Valid BlackWhitelistDTO whiteListDTOBlack) {
        return requestService.addWhitelist(whiteListDTOBlack);
    }

    @DeleteMapping("/whitelist")
    public MessageResponse deleteWhiteList(String type, String value) {
        if (StringUtils.isAnyBlank(type, value)) {
            return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteWhitelist(type, value);
    }

    @GetMapping("/blacklist/all")
    public DataResponse queryBlacklist() {
        return requestService.queryBlacklist();
    }

    @PostMapping("/blacklist")
    public MessageResponse addBlacklist(@RequestBody @Valid BlackWhitelistDTO BlackWhitelistDTO) {
        return requestService.addBlacklist(BlackWhitelistDTO);
    }

    @DeleteMapping("/blacklist")
    public MessageResponse deleteBlacklist(String type, String value) {
        if (StringUtils.isAnyBlank(type, value)) {
            return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteBlacklist(type, value);
    }







}
