package com.hqy.global;

import com.hqy.common.bind.MessageResponse;
import com.hqy.common.result.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
public class WebMvcGlobalExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public MessageResponse handler(RuntimeException e) {
        log.error(e.getMessage(), e);
        return new MessageResponse(false, CommonResult.SYSTEM_ERROR.message, CommonResult.SYSTEM_ERROR.code);
    }



    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public MessageResponse handler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().get(0);
        return new MessageResponse(false, objectError.getDefaultMessage(), CommonResult.ERROR_PARAM.code);
    }


}
