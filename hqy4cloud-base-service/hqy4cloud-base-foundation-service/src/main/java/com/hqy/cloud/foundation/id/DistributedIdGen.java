package com.hqy.cloud.foundation.id;

import com.alibaba.nacos.api.exception.NacosException;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.swticher.CommonSwitcher;
import com.hqy.cloud.id.service.RemoteLeafService;
import com.hqy.cloud.id.struct.ResultStruct;
import com.hqy.cloud.rpc.nacos.client.RPCClient;
import com.hqy.cloud.rpc.nacos.utils.NacosConfigurationUtils;
import com.hqy.cloud.util.identity.ProjectSnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 分布式id成器
 * 主要调用rpc - id service获取分布式id
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/22 15:18
 */
@Slf4j
public class DistributedIdGen {

    /**
     * 获取分布式leaf分段id
     */
    public static long getSegmentId(String key) {
        if (StringUtils.isBlank(key)) {
            throw new UnsupportedOperationException("BizTag key should not be empty.");
        }
        RemoteLeafService leafService = RPCClient.getRemoteService(RemoteLeafService.class);
        ResultStruct struct = leafService.getSegmentId(key);
        if (!struct.isResult()) {
            throw new IllegalArgumentException("Failed execute to get segment id, result code = " + struct.id);
        }
        return struct.id;
    }
    
    
    /**
     * 获取分布式雪花id
     * @return id.
     */
    public static long getSnowflakeId() {
        try {
            RemoteLeafService leafService = RPCClient.getRemoteService(RemoteLeafService.class);
            ResultStruct struct = leafService.getSnowflakeNextId();
            if (!struct.result) {
                log.warn("Failed execute to get id by rpc, error code: {}.", struct.id);
            } else {
                return struct.id;
            }
        } catch (Throwable exception) {
            log.warn("Failed execute to get id by rpc, cause: {}.", exception.getMessage(), exception);
        }

        if (CommonSwitcher.ENABLE_USING_PID_SNOWFLAKE_WORKER_ID.isOn()) {
            return ProjectSnowflakeIdWorker.getInstance().nextId();
        }

        throw new UnsupportedOperationException("No supported distributed id.");
    }

    public static void main(String[] args) throws NacosException, InterruptedException {
        /*CountDownLatch countDownLatch = new CountDownLatch(1);
        String SERVER_NAME = "test";
        String GROUP_NAME = "DEV_GTOUP";

        Properties nacosProperties = new Properties();
        nacosProperties.setProperty(SERVER_ADDR, "127.0.0.1:8848");
        nacosProperties.put(NAMESPACE, "9cd8de3b-030a-49f1-9256-f04de35cdb9e");
        NamingService namingService = NacosFactory.createNamingService(nacosProperties);

        Instance instance = new Instance();
        instance.setIp(IpUtil.getHostAddress());
        instance.setEphemeral(false);
        instance.setPort(7878);
        instance.setMetadata(new HashMap<>(1));
        instance.getMetadata().put(PreservedMetadataKeys.INSTANCE_ID_GENERATOR, Constants.SNOWFLAKE_INSTANCE_ID_GENERATOR);
        namingService.registerInstance(SERVER_NAME, GROUP_NAME, instance);

        //获取实例列表
        List<Instance> idInstances = namingService.getAllInstances(SERVER_NAME, GROUP_NAME);
        System.out.println(idInstances);

        countDownLatch.await();*/

        System.setProperty(NacosConfigurationUtils.NACOS_ADDRESS_KEY, "hongqy1024.cn:8848");
        long snowflakeId = getSnowflakeId();
        System.out.println(snowflakeId);

        long segmentId = getSegmentId(MicroServiceConstants.ACCOUNT_SERVICE);
        System.out.println(segmentId);


    }





}
