package com.hqy.cloud.web.annotation;

import java.lang.annotation.*;

/**
 * 基础web加强注解
 * @author hongqy
 * @date 2025/12/15
 */
@Documented
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface BsWebAdvice {

    /**
     * 是否需要access_token访问 默认需要
     * @return
     */
    boolean requiredToken() default true;

    /**
     * 死否需要校验全局幂等 默认不需要
     * @return
     */
    boolean requiredIdentifier() default false;
}
