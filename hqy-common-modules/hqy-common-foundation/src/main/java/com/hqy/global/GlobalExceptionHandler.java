package com.hqy.global;

import com.hqy.common.bind.MessageResponse;
import com.hqy.common.result.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Spring mvc全局异常处理
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-09 19:28
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public MessageResponse handler(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new MessageResponse(false, CommonResult.SYSTEM_ERROR.message, CommonResult.SYSTEM_ERROR.code);
    }



}
