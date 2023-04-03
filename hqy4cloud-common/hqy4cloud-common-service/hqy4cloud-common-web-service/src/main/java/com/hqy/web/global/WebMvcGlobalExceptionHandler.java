package com.hqy.web.global;

import com.hqy.cloud.common.base.lang.exception.NotAuthenticationException;
import com.hqy.cloud.common.bind.MessageResponse;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.foundation.common.enums.ExceptionType;
import com.hqy.foundation.spring.event.ExceptionCollActionEvent;
import com.hqy.cloud.util.IpUtil;
import com.hqy.cloud.util.spring.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.util.Objects;

/**
 * Spring mvc全局异常处理
 * @author qy
 * @date 2021-08-09 19:28
 */
@ControllerAdvice
public class WebMvcGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebMvcGlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(NotAuthenticationException.class)
    public MessageResponse handle(NotAuthenticationException exception, HttpServletRequest request) {
        return ResultCode.messageResponse(ResultCode.INVALID_ACCESS_TOKEN);
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public MessageResponse handler(RuntimeException e, HttpServletRequest request) {
        log.error(e.getMessage(), e);
        collectionException(e, request);
        return ResultCode.messageResponse(ResultCode.SYSTEM_ERROR);
    }

    private void collectionException(RuntimeException e, HttpServletRequest request) {
        String requestIp = IpUtil.getRequestIp(request);
        String url = request.getRequestURL().toString();
        ExceptionCollActionEvent event = new ExceptionCollActionEvent(ExceptionType.WEB, this.getClass().getName(), e, 10);
        event.setIp(requestIp);
        event.setUrl(url);
        SpringContextHolder.publishEvent(event);
    }

    /**
     * bean校验未通过异常
     * @param e MethodArgumentNotValidException
     * @return MessageResponse
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public MessageResponse handler(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = "["+ Objects.requireNonNull(e.getBindingResult().getFieldError()).getField()+"] ";
        String defaultMessage = e.getBindingResult().getFieldError().getDefaultMessage();
        if (StringUtils.isNotBlank(defaultMessage)) {
            message = message + defaultMessage;
        } else {
            message = message + "should not be empty.";
        }

        return new MessageResponse(false, message, ResultCode.ERROR_PARAM.code);
    }

    /**
     * javax.validation.ValidationException 捕获
     * @param e ValidationException
     * @return MessageResponse
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseBody
    public MessageResponse validationException(ValidationException  e) {
        log.warn(e.getMessage());
        return ResultCode.messageResponse(ResultCode.ERROR_PARAM);
    }

}
