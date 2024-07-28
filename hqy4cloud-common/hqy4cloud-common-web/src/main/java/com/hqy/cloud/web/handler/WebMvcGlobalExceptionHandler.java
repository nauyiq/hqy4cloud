package com.hqy.cloud.web.handler;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.hqy.cloud.collection.core.exception.ExceptionCollActionEvent;
import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.base.lang.exception.NotAuthenticationException;
import com.hqy.cloud.common.base.lang.exception.RpcException;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.web.utils.IpUtil;
import com.hqy.cloud.common.enums.ExceptionType;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Map;

/**
 * Spring mvc全局异常处理
 * @author qy
 * @date 2021-08-09 19:28
 */
@ControllerAdvice
public class WebMvcGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebMvcGlobalExceptionHandler.class);

    /**
     * 认证未通过异常处理
     * @param exception
     * @param request
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(NotAuthenticationException.class)
    public R<Boolean> handle(NotAuthenticationException exception, HttpServletRequest request) {
        return R.failed(ResultCode.INVALID_ACCESS_TOKEN);
    }

    /**
     * 业务异常处理
     * @param e
     * @param request
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(BizException.class)
    public R<Boolean> handler(BizException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        // 异常采集
//        collectionException(e, request);
        return R.setResult(false,  e.getCode(), e.getMessage(), null);
    }

    /**
     * 全局业务异常处理， 内部异常
     * @param e
     * @param request
     * @return
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public R<Boolean> handler(RuntimeException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        // 异常采集
        collectionException(e, request);
        return R.failed(ResultCode.SYSTEM_ERROR);
    }

    @ResponseBody
    @ExceptionHandler(RpcException.class)
    public R<Boolean> handler(RpcException rpcException, HttpServletRequest request) {
        if (rpcException.isBiz()) {
            return R.failed(ResultCode.RPC_INTERFACE_TOO_MANY_REQUEST);
        } else {
            return R.failed(ResultCode.SYSTEM_BUSY);
        }
    }

    private void collectionException(RuntimeException e, HttpServletRequest request) {
        String requestIp = IpUtil.getRequestIp(request);
        String url = request.getRequestURL().toString();
        ExceptionCollActionEvent event = new ExceptionCollActionEvent(ExceptionType.WEB, this.getClass().getName(), e, 10);
        event.setIp(requestIp);
        event.setUrl(url);
        SpringUtil.publishEvent(event);
    }

    /**
     * bean校验未通过异常
     * @param e MethodArgumentNotValidException
     * @return MessageResponse
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Boolean> handler(MethodArgumentNotValidException e) {
        Map<String, String> errors = Maps.newHashMapWithExpectedSize(1);
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return R.failed(JSONUtil.toJsonStr(errors), ResultCode.ERROR_PARAM.code);
    }

    /**
     * javax.validation.ValidationException 捕获
     * @param e ValidationException
     * @return MessageResponse
     */
   /* @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public R<Boolean> validationException(ValidationException  e) {
        log.warn(e.getMessage());
        return R.failed(ResultCode.ERROR_PARAM);
    }*/

}
