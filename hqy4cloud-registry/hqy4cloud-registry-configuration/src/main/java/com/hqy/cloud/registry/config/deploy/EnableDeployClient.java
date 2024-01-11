package com.hqy.cloud.registry.config.deploy;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.registry.common.Constants;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/5
 */
@Target(ElementType.TYPE)
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Import(EnableDeployClientImportSelector.class)
public @interface EnableDeployClient {

    boolean enableDeploy() default true;

    ActuatorNode actuatorType() default ActuatorNode.CONSUMER;

    String revision() default Constants.DEFAULT_REVISION;

    int weight() default Constants.DEFAULT_WEIGHT;



}
