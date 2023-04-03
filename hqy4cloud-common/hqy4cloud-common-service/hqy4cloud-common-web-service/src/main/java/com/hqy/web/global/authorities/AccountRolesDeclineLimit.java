package com.hqy.web.global.authorities;

import java.lang.annotation.*;

/**
 * 角色权限校验注解 基于RBAC模型.
 * 标注在某个controller类或方法上
 * 1. 如果是标注在类上则表示当前类下的所有controller接口都需要判断用户访问该接口是否有权限
 * 2. 标注在controller方法上 表示被标注的接口需要校验用户权限
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/10/24 9:28
 */
@Documented
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccountRolesDeclineLimit {






}
