package com.hqy.cloud.actuator.service;

import com.hqy.cloud.actuator.model.MicroServerSwitcherInfo;

import java.util.Map;

/**
 * 服务治理service
 * 开关升降级 + 灰白度控制
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/17 18:02
 */
public interface MicroServiceGradeManageService {

    /**
     * return server grade info.
     * @return server grade info.
     */
    Map<String, Object> getServerGradeInfo();

    /**
     * 更改服务节点的灰白度
     * @param grayOrWhiteValue 灰度或白度值 {@link com.hqy.cloud.rpc.model.PubMode#GRAY} OR {@link com.hqy.cloud.rpc.model.PubMode#WHITE}
     */
    void changeServerPubModeValue(int grayOrWhiteValue);

    /**
     * 获取当前服务注册的服务开关信息
     * @return switcher info
     */
    Map<String, Object> getServerSwitcherInfo();

    /**
     * 更改服务某个开关的状态
     * @param microServerSwitcherInfo 开关信息
     */
    void changeServerSwitcher(MicroServerSwitcherInfo microServerSwitcherInfo);



}
