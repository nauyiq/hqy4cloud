package com.hqy4cloud.dynamics.annotation;

import java.lang.annotation.*;

/**
 * 开启动态数据源
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/15 15:32
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableDynamicDataSource {
}
