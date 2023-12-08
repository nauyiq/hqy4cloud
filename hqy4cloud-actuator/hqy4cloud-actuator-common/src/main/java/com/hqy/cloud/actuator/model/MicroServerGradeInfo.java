package com.hqy.cloud.actuator.model;

import java.io.Serializable;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 15:20
 */
public class MicroServerGradeInfo implements Serializable {

    /**
     * 服务名， spring.application.name
     */
    private String name;

    /**
     * 服务别名，比如网关服务, 账号认证服务...
     */
    private String alias;

    /**
     * actuator类型， CONSUMER OR PROVIDER
     */
    private String actuatorType;

    /**
     * 灰白度发布的值？ 灰度 or 白度？
     */
    private Integer pubMode;


}
