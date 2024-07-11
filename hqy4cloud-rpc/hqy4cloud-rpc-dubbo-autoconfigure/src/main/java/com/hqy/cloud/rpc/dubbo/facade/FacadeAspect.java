package com.hqy.cloud.rpc.dubbo.facade;

import com.alibaba.fastjson2.JSON;
import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.bind.Response;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.util.BeanValidator;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Facade 切面处理类， 统一进行参数校验和异常捕获，采集
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/9
 */
@Slf4j
@Aspect
@Component
@SuppressWarnings({"unchecked", "rawtypes"})
public class FacadeAspect {

    @Around("@annotation(com.hqy.cloud.rpc.dubbo.facade.Facade)")
    public Object facade(ProceedingJoinPoint pjp) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args = pjp.getArgs();
        if (log.isDebugEnabled()) {
            log.debug("start to execute , method = {} , args = {}", method.getName(), JSON.toJSONString(args));
        }

        Class returnType = ((MethodSignature) pjp.getSignature()).getMethod().getReturnType();

        //循环遍历所有参数，进行参数校验
        for (Object parameter : args) {
            try {
                BeanValidator.validateObject(parameter);
            } catch (ValidationException e) {
                printLog(stopWatch, method, args, "failed to validate", null, e);
                return getFailedResponse(returnType, e);
            }
        }

        try {
            // 目标方法执行
            Object response = pjp.proceed();
            enrichObject(response);
            printLog(stopWatch, method, args, "end to execute", response, null);
            return response;
        } catch (Throwable throwable) {
            // TODO 异常采集

            // 如果执行异常，则返回一个失败的response
            printLog(stopWatch, method, args, "failed to execute", null, throwable);
            return getFailedResponse(returnType, throwable);
        }

    }


    /**
     * 日志打印
     *
     * @param stopWatch
     * @param method
     * @param args
     * @param action
     * @param response
     */
    private void printLog(StopWatch stopWatch, Method method, Object[] args, String action, Object response,
                          Throwable throwable) {
        try {
            //因为此处有JSON.toJSONString，可能会有异常，需要进行捕获，避免影响主干流程
            log.info(getInfoMessage(action, stopWatch, method, args, response, throwable), throwable);
            // 如果校验失败，则返回一个失败的response
        } catch (Exception e1) {
            log.error("log failed", e1);
        }
    }


    /**
     * 统一格式输出，方便做日志统计
     * <p>
     * *** 如果调整此处的格式，需要同步调整日志监控 ***
     *
     * @param action    行为
     * @param stopWatch 耗时
     * @param method    方法
     * @param args      参数
     * @param response  响应
     * @return 拼接后的字符串
     */
    private String getInfoMessage(String action, StopWatch stopWatch, Method method, Object[] args, Object response,
                                  Throwable exception) {

        StringBuilder stringBuilder = new StringBuilder(action);
        stringBuilder.append(" ,method = ");
        stringBuilder.append(method.getName());
        stringBuilder.append(" ,cost = ");
        stringBuilder.append(stopWatch.getTime()).append(" ms");
        if (response instanceof Response) {
            stringBuilder.append(" ,success = ");
            stringBuilder.append(((Response) response).isResult());
        }
        if (exception != null) {
            stringBuilder.append(" ,success = ");
            stringBuilder.append(false);
        }
        stringBuilder.append(" ,args = ");
        stringBuilder.append(JSON.toJSONString(Arrays.toString(args)));

        if (response != null) {
            stringBuilder.append(" ,resp = ");
            stringBuilder.append(JSON.toJSONString(response));
        }

        if (exception != null) {
            stringBuilder.append(" ,exception = ");
            stringBuilder.append(exception.getMessage());
        }

        if (response instanceof Response baseResponse) {
            if (!baseResponse.isResult()) {
                stringBuilder.append(" , execute_failed");
            }
        }

        return stringBuilder.toString();
    }

    /**
     * 将response的信息补全，主要是code和message
     * @param response
     */
    private void enrichObject(Object response) {
        if (response instanceof Response) {
            if (!((Response) response).isResult()) {
                //如果状态是成功的，需要将未设置的responseCode设置成BIZ_ERROR
                if (StringUtils.isEmpty(((Response) response).getMessage())) {
                    ((Response) response).setMessage(ResultCode.FAILED.getMessage());
                }
            }
        }
    }

    /**
     * 定义并返回一个通用的失败响应
     */
    private Object getFailedResponse(Class returnType, Throwable throwable)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {

        //如果返回值的类型为BaseResponse 的子类，则创建一个通用的失败响应
        if (returnType.getDeclaredConstructor().newInstance() instanceof Response response) {
            response.setResult(false);
            if (throwable instanceof BizException bizException) {
                response.setMessage(ResultCode.SYSTEM_BUSY.getMessage());
                response.setCode(bizException.getCode());
            } else {
                response.setMessage(throwable.toString());
                response.setCode(ResultCode.FAILED.code);
            }
            return response;
        }
        log.error("failed to getFailedResponse , returnType ({}) is not instanceof BaseResponse", returnType);
        return null;
    }


}
