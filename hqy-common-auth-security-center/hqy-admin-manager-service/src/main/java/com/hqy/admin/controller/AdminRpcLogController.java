package com.hqy.admin.controller;

import com.hqy.admin.service.request.AdminRpcLogRequestService;
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
 * @date 2023/1/9 14:14
 */
@Slf4j
@RestController
@RequestMapping("/admin/rpc")
@RequiredArgsConstructor
public class AdminRpcLogController {

    private final AdminRpcLogRequestService requestService;


    @GetMapping("/flow/page")
    public DataResponse queryRpcFlowPage(String caller, String provider, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return requestService.queryRpcFlowPage(caller, provider, current, size);
    }

    @DeleteMapping("/flow/{id}")
    public MessageResponse deleteRpcFlowRecord(@PathVariable("id") Long id) {
        if (Objects.isNull(id)) {
            return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteRpcFlowRecord(id);
    }

    @GetMapping("/error/page")
    public DataResponse queryRpcErrorPage(String application, String serviceClassName, Integer type, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return requestService.queryRpcErrorPage(application, serviceClassName, type, current, size);
    }

    @DeleteMapping("/error/{id}")
    public MessageResponse deleteRpcExceptionRecord(@PathVariable("id") Long id) {
        if (Objects.isNull(id)) {
            return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM_UNDEFINED);
        }
        return requestService.deleteRpcExceptionRecord(id);
    }




}
