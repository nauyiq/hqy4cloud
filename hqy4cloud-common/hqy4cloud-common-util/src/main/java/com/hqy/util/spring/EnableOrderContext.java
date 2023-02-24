package com.hqy.util.spring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 需要在启动类引用此注解, 表示优先加载SpringContextHolder 这个bean
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/1 13:45
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({SpringApplicationConfiguration.class, SpringContextHolderProcessor.class})
public @interface EnableOrderContext {
}
