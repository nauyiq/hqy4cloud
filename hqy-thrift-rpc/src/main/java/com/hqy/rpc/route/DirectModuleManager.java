package com.hqy.rpc.route;

import com.hqy.fundation.common.base.project.UsingIpPort;

/**
 * 直连模块管理 判断当前服务是否是直连服务等。
 * 既直接获取调试数据中的指定的ip端口 方便rpc直连场景使用
 * @author qiyuan.hong
 * @date 2022-02-25 22:32
 */
public interface DirectModuleManager {

    /**
     * 通过注册到远程服务的模块英文名获取直连的节点数据
     * @param moduleNameEn 模块英文名
     * @return 节点信息
     */
    UsingIpPort getDirectUip(String moduleNameEn);


    /**
     * 判断项目是否配置直连服务
     * @param moduleName 模块名
     * @return Boolean
     */
    boolean isDirect(String moduleName);




}
