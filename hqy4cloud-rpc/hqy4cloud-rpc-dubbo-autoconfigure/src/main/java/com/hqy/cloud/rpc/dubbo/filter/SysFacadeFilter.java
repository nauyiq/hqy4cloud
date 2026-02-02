package com.hqy.cloud.rpc.dubbo.filter;

import com.alibaba.fastjson2.JSON;
import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.response.Response;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.util.BeanValidator;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author hongqy
 * @date 2026/1/30
 */
@Slf4j
@Activate(group = CommonConstants.PROVIDER)
public class SysFacadeFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 获取执行的方法参数
        Object[] arguments = invocation.getArguments();
        // 获取响应类型
        Class<?> returnType;
        try {
            Class<?> invokerInterface = invoker.getInterface();
            Method method = invokerInterface.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
            returnType = method.getReturnType();
        } catch (NoSuchMethodException e) {
            throw new RpcException("No such method: " + invocation.getMethodName(), e);
        }

        //循环遍历所有参数，进行参数校验
        for (Object parameter : arguments) {
            try {
                BeanValidator.validateObject(parameter);
            } catch (ValidationException e) {
                printLog(stopWatch, invocation.getMethodName(), arguments, "Failed to validate", null, e);
                return getFailedResponse(returnType, e, true);
            }
        }

        try {
            // 目标方法执行
            Result result = invoker.invoke(invocation);
            // 补全响应消息
            enrichObject(result.getValue());
            // 打印日志
            printLog(stopWatch, invocation.getMethodName(), arguments, "Rpc execute to", result.getValue(), null);

            return result;
        } catch (Exception e) {
            // 如果执行异常，则返回一个失败的response
            printLog(stopWatch, invocation.getMethodName(), arguments, "Failed to execute", null, e);
            return getFailedResponse(returnType, e, false);
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
    private void printLog(StopWatch stopWatch, String method, Object[] args, String action, Object response,
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
    private String getInfoMessage(String action, StopWatch stopWatch, String method, Object[] args, Object response,
                                  Throwable exception) {

        StringBuilder stringBuilder = new StringBuilder(action);
        stringBuilder.append(", method = ");
        stringBuilder.append(method);
        stringBuilder.append(", cost = ");
        stringBuilder.append(stopWatch.getTime()).append(" ms");
        if (response instanceof Response) {
            stringBuilder.append(", success = ");
            stringBuilder.append(((Response) response).isSuccess());
        }
        if (exception != null) {
            stringBuilder.append(", success = ");
            stringBuilder.append(false);
        }
        stringBuilder.append(", args = ");
        stringBuilder.append(JSON.toJSONString(Arrays.toString(args)));

        if (response != null) {
            stringBuilder.append(", resp = ");
            stringBuilder.append(JSON.toJSONString(response));
        }

        if (exception != null) {
            stringBuilder.append(", exception = ");
            stringBuilder.append(exception.getMessage());
        }

        if (response instanceof Response baseResponse) {
            if (!baseResponse.isSuccess()) {
                stringBuilder.append(", execute_failed");
            }
        }

        return stringBuilder.toString();
    }


    /**
     * 定义并返回一个通用的失败响应
     */
    private Result getFailedResponse(Class returnType, Throwable throwable, boolean valid)
            throws RpcException {

        //如果返回值的类型为BaseResponse 的子类，则创建一个通用的失败响应
        try {
            if (returnType.getDeclaredConstructor().newInstance() instanceof Response response) {
                response.setSuccess(false);
                if (valid) {
                    // 校验入参异常
                    response.setCode(ResultCode.PARAMS_ERROR.getCode());
                    response.setMessage(ResultCode.PARAMS_ERROR.getMessage());
                } else if (throwable instanceof BizException bizException) {
                    response.setCode(bizException.getCode());
                    response.setMessage(bizException.getMessage());
                } else {
                    response.setMessage(ResultCode.SYSTEM_INTERVAL_ERROR.message);
                    response.setCode(ResultCode.SYSTEM_INTERVAL_ERROR.code);
                }
                return new AppResponse(response);
            }
        } catch (Exception e) {
            throw new RpcException(e);
        }
        log.error("failed to getFailedResponse , returnType ({}) is not instanceof BaseResponse", returnType);
        return null;
    }

    /**
     * 将response的信息补全，主要是code和message
     * @param response
     */
    private void enrichObject(Object response) {
        if (response instanceof Response) {
            if (!((Response) response).isSuccess()) {
                //如果状态是成功的，需要将未设置的responseCode设置成BIZ_ERROR
                if (StringUtils.isEmpty(((Response) response).getMessage())) {
                    ((Response) response).setMessage(ResultCode.FAILED.getMessage());
                }
            }
        }
    }


}
