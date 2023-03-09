package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminErrorLogService;
import com.hqy.cloud.auth.core.authentication.PreAuthentication;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.coll.struct.PageExceptionLogStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static com.hqy.cloud.common.result.CommonResultCode.ERROR_PARAM_UNDEFINED;

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
    public R<PageExceptionLogStruct> pageErrorLog(String serviceName, String type, String environment, String exceptionClass, String ip, String url, Integer current, Integer size) {
        return requestService.pageErrorLog(serviceName, type, environment, exceptionClass, ip, url, current, size);
    }

    @DeleteMapping("{id}")
    @PreAuthentication("sys_log_error_del")
    public R<Boolean> deleteErrorLog(@PathVariable("id") Long id) {
        if (Objects.isNull(id)) {
            R.failed(ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteErrorLog(id);
    }


}
