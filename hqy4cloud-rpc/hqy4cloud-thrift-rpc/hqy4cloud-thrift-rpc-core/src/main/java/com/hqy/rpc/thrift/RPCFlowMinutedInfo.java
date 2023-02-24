/*
package com.hqy.rpc.thrift;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * rpc流量窗口信息 一分钟
 * @author qiyuan.hong
 * @date 2022-03-17 17:37
 *//*

@Data
@ToString
@NoArgsConstructor
public class RPCFlowMinutedInfo {

    */
/**
     * 调用者
     *//*

    private String caller;

    */
/**
     * 调用的总次数
     *//*

    public int total = 0;

    */
/**
     * rpc方法调用失败的次数
     *//*

    public int ngTotal = 0;

    */
/**
     * 当前的时间窗口 格式到分 即hh-MM-dd HH:mm
     *//*

    private String window;

    */
/**
     * 接口分组的计数map
     *//*

    public final Map<String, Integer> serviceMap = new HashMap<>();

    */
/**
     * 方法分组的计数map
     *//*

    public final Map<String, Integer> methodMap = new HashMap<>();

    public RPCFlowMinutedInfo(String caller, String window) {
        this.caller = caller;
        this.window = window;
    }
}
*/
