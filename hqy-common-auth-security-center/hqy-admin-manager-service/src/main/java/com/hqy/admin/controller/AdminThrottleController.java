package com.hqy.admin.controller;

import com.hqy.admin.service.request.AdminThrottleRequestService;
import com.hqy.base.common.bind.DataResponse;
import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/5 14:59
 */
@Slf4j
@RestController
@RequestMapping("/admin/log/throttle")
@RequiredArgsConstructor
public class AdminThrottleController {

    private final AdminThrottleRequestService service;

    @GetMapping("/page")
    public DataResponse getPageThrottledHistory(String throttleBy, String ip, String url,  Integer current, Integer size) {
        return service.getPageThrottledHistory(throttleBy, ip, url, current, size);
    }

    @DeleteMapping("{id}")
    public MessageResponse deleteThrottledHistory(@PathVariable("id") Long id) {
        if (Objects.isNull(id)) {
            return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return service.deleteThrottledHistory(id);
    }

}
