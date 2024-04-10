package com.hqy.cloud.foundation.domain;

import com.hqy.cloud.registry.context.ProjectContext;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/4/8
 */
public interface DomainServer {

    /**
     * 获取域名
     * @param scene 场景, 项目名..
     * @return      返回配置的域名
     */
    default String getDomain(String scene) {
        return this.getDomain(ProjectContext.getEnvironment().getEnvironment(), scene);
    }

    /**
     * 获取域名.
     * @param env    环境.
     * @param scene  场景, 项目名..
     * @return       返回配置的域名.
     */
    String getDomain(String env, String scene);

}
