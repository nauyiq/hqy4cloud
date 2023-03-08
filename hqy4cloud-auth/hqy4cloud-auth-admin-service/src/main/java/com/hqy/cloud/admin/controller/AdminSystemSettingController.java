package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminSystemSettingService;
import com.hqy.cloud.auth.core.authentication.PreAuthentication;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.auth.base.dto.BlackWhitelistDTO;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

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
    public R<Set<BlackWhitelistDTO>> queryWhiteList() {
        return requestService.queryWhitelist();
    }

    @PostMapping("/whitelist")
    @PreAuthentication("sys_white_add")
    public R<Boolean> addWhiteList(@RequestBody @Valid BlackWhitelistDTO whiteListDTOBlack) {
        return requestService.addWhitelist(whiteListDTOBlack);
    }

    @DeleteMapping("/whitelist")
    @PreAuthentication("sys_white_del")
    public R<Boolean> deleteWhiteList(String type, String value) {
        if (StringUtils.isAnyBlank(type, value)) {
            return R.failed(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteWhitelist(type, value);
    }

    @GetMapping("/blacklist/all")
    public R<Set<BlackWhitelistDTO>> queryBlacklist() {
        return requestService.queryBlacklist();
    }

    @PostMapping("/blacklist")
    @PreAuthentication("sys_black_add")
    public R<Boolean> addBlacklist(@RequestBody @Valid BlackWhitelistDTO BlackWhitelistDTO) {
        return requestService.addBlacklist(BlackWhitelistDTO);
    }

    @DeleteMapping("/blacklist")
    @PreAuthentication("sys_black_del")
    public R<Boolean> deleteBlacklist(String type, String value) {
        if (StringUtils.isAnyBlank(type, value)) {
            return R.failed(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteBlacklist(type, value);
    }







}
