package com.hqy.cloud.admin.controller;

import com.hqy.cloud.admin.service.RequestAdminRpcLogService;
import com.hqy.cloud.auth.core.authentication.PreAuthentication;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.coll.struct.PageRpcExceptionRecordStruct;
import com.hqy.cloud.coll.struct.PageRpcFlowRecordStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    private final RequestAdminRpcLogService requestService;


    @GetMapping("/flow/page")
    public R<PageRpcFlowRecordStruct> queryRpcFlowPage(String caller, String provider, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return requestService.queryRpcFlowPage(caller, provider, current, size);
    }

    @DeleteMapping("/flow/{id}")
    @PreAuthentication("sys_rpc_flow_log_del")
    public R<Boolean> deleteRpcFlowRecord(@PathVariable("id") Long id) {
        return requestService.deleteRpcFlowRecord(id);
    }

    @GetMapping("/error/page")
    public R<PageRpcExceptionRecordStruct> queryRpcErrorPage(String application, String serviceClassName, Integer type, Integer current, Integer size) {
        current = current == null ? 1 : current;
        size = size == null ? 20 : size;
        return requestService.queryRpcErrorPage(application, serviceClassName, type, current, size);
    }

    @DeleteMapping("/error/{id}")
    @PreAuthentication("sys_rpc_error_log_del")
    public R<Boolean> deleteRpcExceptionRecord(@PathVariable("id") Long id) {
        return requestService.deleteRpcExceptionRecord(id);
    }




}
