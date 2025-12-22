package com.hqy.cloud.auth.annotation;

import java.lang.annotation.*;

/**
 * 定义权限资源
 * @author hongqy
 * @date 2025/12/15
 */
@Documented
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthenticationResource {

    /**
     * 资源ID
     * <pre>
     *     1. 如果是HTTP请求，则默认用请求方法类型 + 请求URI 作为资源的唯一标识
     *     2. 如果是RPC请求，则默认用RPC服务名 + 方法名作为资源的唯一标识
     * </pre>
     * @return
     */
    String id() default "";

    /**
     * 资源需要的权限
     * @return
     */
    String[] authorities();

}
