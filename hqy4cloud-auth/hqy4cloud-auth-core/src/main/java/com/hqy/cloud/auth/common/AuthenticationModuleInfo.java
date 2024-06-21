package com.hqy.cloud.auth.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/6/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationModuleInfo implements Serializable {

    /**
     * 角色、权限
     */
    private String authority;

    /**
     * 对应的模块权限
     */
    private List<ModuleInfo> moduleInfos;



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ModuleInfo {

        /**
         * 资源表达式、 如/oauth/**
         */
        private String moduleExpression;

        /**
         * 支持的http method， 默认为空表示都支持
         */
        private String method;

    }

}
