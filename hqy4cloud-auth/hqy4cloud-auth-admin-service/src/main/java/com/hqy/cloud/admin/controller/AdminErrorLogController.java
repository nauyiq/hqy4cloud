package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminErrorLogService;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.result.CommonResultCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/1/6 15:40
 */
@Slf4j
@RestController
@RequestMapping("/admin/log/error")
@RequiredArgsConstructor
public class AdminErrorLogController {

    private final RequestAdminErrorLogService requestService;

    @GetMapping("page")
    public DataResponse pageErrorLog(String serviceName, String type, String environment, String exceptionClass, String ip, String url, Integer current, Integer size) {
        return requestService.pageErrorLog(serviceName, type, environment, exceptionClass, ip, url, current, size);
    }

    @DeleteMapping("{id}")
    public MessageResponse deleteErrorLog(@PathVariable("id") Long id) {
        if (Objects.isNull(id)) {
            return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteErrorLog(id);
    }


}
