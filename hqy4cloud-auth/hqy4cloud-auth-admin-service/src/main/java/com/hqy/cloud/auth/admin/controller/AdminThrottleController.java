package com.hqy.cloud.auth.admin.controller;

import com.hqy.cloud.auth.admin.service.RequestAdminThrottleService;
import com.hqy.cloud.auth.annotation.PreAuthentication;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.coll.struct.PageThrottledBlockResultStruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    private final RequestAdminThrottleService service;

    @GetMapping("/page")
    public R<PageThrottledBlockResultStruct> getPageThrottledHistory(String throttleBy, String ip, String url, Integer current, Integer size) {
        return service.getPageThrottledHistory(throttleBy, ip, url, current, size);
    }

    @DeleteMapping("{id}")
    @PreAuthentication("sys_log_throttle_del")
    public R<Boolean> deleteThrottledHistory(@PathVariable("id") Long id) {
        return service.deleteThrottledHistory(id);
    }

}
