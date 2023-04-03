package com.hqy.cloud.id.component.snowflake.core.support;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.id.component.snowflake.core.AbstractSnowflakeHolder;
import com.hqy.cloud.id.component.snowflake.exception.InitWorkerIdException;
import com.hqy.cloud.util.spring.SpringContextHolder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 获取当前的服务的worker id.
 * 主要注意的是 在调试环境下可以将服务设置非持久性服务, 而在生产环境下必须将服务设置为持久服务
 * 即spring.cloud.nacos.discovery.ephemeral=false 这样可以保证当前服务的生成instance id不会发生变化
 * 注: nacos在2.x版本取消了雪花id的支持 详情请看nacos issue：https://github.com/alibaba/nacos/issues/9001（或许在2.3.0进行该支持）
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/22 14:07
 */
@Slf4j
public class SnowflakeNacosHolder extends AbstractSnowflakeHolder {

    private final NacosDiscoveryProperties nacosDiscoveryProperties;
    private final NacosServiceManager nacosServiceManager;

    public SnowflakeNacosHolder(NacosDiscoveryProperties nacosDiscoveryProperties, NacosServiceManager nacosServiceManager) {
        this.nacosDiscoveryProperties = nacosDiscoveryProperties;
        this.nacosServiceManager = nacosServiceManager;
    }

    private String concatAddr(String ip, int port) {
        return ip + StrUtil.COLON + port;
    }


    @Override
    @SneakyThrows
    protected void doInit(String serviceName)  {
        NamingService namingService = nacosServiceManager.getNamingService();
        // 获取当前服务列表
        List<Instance> instances = namingService.selectInstances(nacosDiscoveryProperties.getService(), nacosDiscoveryProperties.getGroup(), true);
        if (CollectionUtils.isEmpty(instances)) {
            //不存在节点列表， 说明注册服务列表存在异常， 需要检查nacos配置.
            throw new NacosException(ResultCode.FAILED.code, "Id service instances should not be empty.");
        }

        UsingIpPort ipPort = SpringContextHolder.getProjectContextInfo().getUip();
        String serviceAddr = concatAddr(ipPort.getHostAddr(), ipPort.getPort());

        //遍历实例列表获取当前服务的worker id.
        for (Instance instance : instances) {
            String instanceIp = instance.getIp();
            int instancePort = instance.getPort();
            String instanceAddr = concatAddr(instanceIp, instancePort);
            if (serviceAddr.equals(instanceAddr)) {
                this.workerId = Integer.parseInt(instance.getInstanceId());
                break;
            }
        }

        if (this.workerId == -1) {
            //从nacos获取不到自己工作id.
            throw new InitWorkerIdException();
        }

        log.info("Init worker id successful, workerId: {}", workerId);
    }
}
