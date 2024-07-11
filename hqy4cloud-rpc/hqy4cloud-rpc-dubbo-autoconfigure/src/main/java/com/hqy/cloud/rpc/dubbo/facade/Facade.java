package com.hqy.cloud.rpc.dubbo.facade;

import java.lang.annotation.*;

/**
 * dubbo切面注解
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/9
 */
@Documented
@Target({ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Facade {
}
