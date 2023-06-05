package com.hqy.cloud.rpc.nacos.node;

import com.hqy.cloud.rpc.model.RegistryInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册到nacos服务的nacos server信息
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/8 11:09
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NacosServerInfo {
    private RegistryInfo registryInfo;
    private String group;
    private String namespace;


    public String getServerAddr() {
        return registryInfo.getAddress();
    }

    public int getPort() {
        return registryInfo.getPort();
    }


}
