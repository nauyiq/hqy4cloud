package com.hqy.rpc.regist;

import com.hqy.fundation.common.base.project.UsingIpPort;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 节点信息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/16 17:03
 */
@Data
@Slf4j
public abstract class Node {


    /**
     * 节点名称 (中文名)
     */
    private String name;

    /**
     * 节点名称 (英文名)
     */
    private String nameEn;

    /**
     * 使用的ip，端口等信息
     */
    private UsingIpPort uip;

    /**
     * 当前节点在注册中心是否是脱机状态... true表示存活
     */
    private Boolean alive = true;

    /**
     * 灰白度 默认灰度发布
     */
    private int pubValue;

    /**
     * 是否是服务的提供者， 服务的提供者是相对而言并不是绝对的
     * 当暴露rpc服务时 则表示当前服务是服务的提供者
     * @return
     */
    public Boolean isProviderService() {
        if (Objects.isNull(uip)) {
            log.warn("[系统初始化异常] 节点信息未注册.");
            return false;
        }
        int rpcPort = uip.getRpcPort();
        return rpcPort != -1;
    }

    /**
     * 判断当前服务是否提供socket端口
     * @return
     */
    public Boolean isSocketService() {
        if (Objects.isNull(uip)) {
            log.warn("[系统初始化异常] 节点信息未注册.");
            return false;
        }
        int socketPort = uip.getSocketPort();
        return socketPort != -1;
    }


}
