package com.hqy.web.global;

import com.hqy.base.common.bind.MessageResponse;
import com.hqy.base.common.result.CommonResultCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

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
    @ExceptionHandler(RuntimeException.class)
    public MessageResponse handler(RuntimeException e) {
        log.error(e.getMessage(), e);
        return CommonResultCode.messageResponse(CommonResultCode.SYSTEM_ERROR);
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

        return new MessageResponse(false, message, CommonResultCode.ERROR_PARAM.code);
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
        return CommonResultCode.messageResponse(CommonResultCode.ERROR_PARAM);
    }

}
