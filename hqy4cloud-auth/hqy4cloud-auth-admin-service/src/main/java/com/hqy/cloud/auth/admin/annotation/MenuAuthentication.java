package com.hqy.cloud.auth.admin.annotation;

import java.lang.annotation.*;

/**
 * 表示用户需要哪些菜单权限
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/27
 */
@Documented
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MenuAuthentication {

    /**
     * 表示菜单接口需要哪些权限
     * @return 菜单permission
     */
    String value();

}
