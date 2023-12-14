package com.hqy.cloud.db.dynamics.annotation;

import com.hqy.cloud.db.dynamics.config.DynamicDataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

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
@Import(DynamicDataSourceAutoConfiguration.class)
public @interface EnableDynamicDataSource {
}
