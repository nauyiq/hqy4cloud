package com.hqy.cloud.actuator.endpoint;

import com.hqy.cloud.actuator.core.Indicator;
import com.hqy.cloud.actuator.model.MicroServerGradeInfo;
import com.hqy.cloud.actuator.service.MicroServiceGradeManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 服务治理-服务灰白度升降级端点
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/17 17:41
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Endpoint(id = MicroServiceGradeManageEndpoint.ID)
public class MicroServiceGradeManageEndpoint implements Indicator<MicroServerGradeInfo> {
    public static final String ID = "grade";
    private final MicroServiceGradeManageService microServiceGradeManageService;

    @Override
    public String indicatorId() {
        return ID;
    }

    @ReadOperation
    public Map<String, Object> readEndpoint() {
        return microServiceGradeManageService.getServerGradeInfo();
    }

    @WriteOperation
    public void writeEndpoint(Integer pubMode) {
        microServiceGradeManageService.changeServerPubModeValue(pubMode);
    }



}
