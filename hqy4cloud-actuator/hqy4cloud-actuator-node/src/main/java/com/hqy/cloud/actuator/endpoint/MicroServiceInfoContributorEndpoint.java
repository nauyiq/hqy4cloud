package com.hqy.cloud.actuator.endpoint;

import com.hqy.cloud.common.base.lang.ActuatorNode;
import com.hqy.cloud.common.base.project.MicroServiceConstants;
import com.hqy.cloud.common.base.project.ProjectContextInfo;
import com.hqy.cloud.common.base.project.UsingIpPort;
import com.hqy.cloud.registry.context.ProjectContext;
import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

/**
 * info端点指标控制
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/8 14:29
 */
public class MicroServiceInfoContributorEndpoint implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        ProjectContextInfo info = ProjectContext.getContextInfo();
        builder.withDetail("服务名", MicroServiceConstants.ALIAS_MAP.get(info.getNameEn()))
                .withDetail("环境", info.getEnv())
                .withDetail("节点类型", info.getNodeType().alias);
        UsingIpPort uip = info.getUip();
        if (uip != null && info.getNodeType() == ActuatorNode.PROVIDER) {
            builder.withDetail("RPC端口", uip.getRpcPort());
        }
        builder.withDetail("元数据", info.getMetadata());
    }
}
