package com.hqy.cloud.web.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 文件上传方式
 * 同步上传还是异步上传.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 14:48
 */
@Documented
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface UploadMode {

    Mode value() default Mode.SYNC;

    enum Mode {

        /**
         * 同步
         */
        SYNC,

        /**
         * 异步
         */
        ASYNC,

    }

}
