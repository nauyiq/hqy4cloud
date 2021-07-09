package com.hqy.util.dto;

import lombok.Data;

/**
 * @author qy
 * @description:
 * @project: hqy-parent
 * @create 2021-07-08 17:32
 */
@Data
public class RequestMessagePacket extends BaseMessagePacket {


    /**
     * 接口全类名
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法参数签名
     */
    private String[] methodArgumentSignatures;

    /**
     * 方法参数
     */
    private Object[] methodArguments;

}
