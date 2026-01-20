package com.hqy.cloud.web.aspect;

import cn.hutool.json.JSONUtil;
import com.github.houbb.sensitive.core.api.SensitiveUtil;
import com.hqy.cloud.common.response.MultiResponse;
import com.hqy.cloud.common.response.SingleResponse;
import com.hqy.cloud.common.result.R;
import com.hqy.cloud.web.annotation.BsWebAdvice;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author hongqy
 * @date 2026/1/15
 */
@Slf4j
@Aspect
@Component
public class BsWebAdviceAspect {

    @Around("@annotation(com.hqy.cloud.web.annotation.BsWebAdvice)")
    public Object facade(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        BsWebAdvice bsWebAdvice = method.getAnnotation(BsWebAdvice.class);
        Object response = pjp.proceed();
        if (bsWebAdvice.desensitize()) {
            // 如果需要脱敏展示数据， 则脱敏后返回
            return desensitize(response);
        }
        return response;
    }

    /**
     * 脱敏处理, 基于sensitive
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private Object desensitize(Object result) {
        if (result instanceof R<?>) {
            R r = (R) result;
            Object desCopyData = SensitiveUtil.desCopy(r.getData());
            r.setData(desCopyData);
            return r;
        } else if (result instanceof SingleResponse response) {
            Object desCopyData = SensitiveUtil.desCopy(response.getData());
            response.setData(desCopyData);
            return response;
        } else if (result instanceof MultiResponse multipleResponse) {
            List desCopyCollection = SensitiveUtil.desCopyCollection(multipleResponse.getData());
            multipleResponse.setData(desCopyCollection);
            return multipleResponse;
        } else if (result instanceof String && (JSONUtil.isTypeJSON((String) result) || JSONUtil.isTypeJSONArray((String)  result))) {
            return SensitiveUtil.desJson(result);
        } else {
            return SensitiveUtil.desCopy(result);
        }
    }

}
