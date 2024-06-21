package com.hqy.cloud.auth.annotation;


import java.lang.annotation.*;

/**
 * 表示接口需要哪些权限.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/8
 */
@Documented
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PreAuthentication {

    /**
     * 需要的权限值
     * @return 权限
     */
    String value();

}
