package com.hqy.cloud.actuator.endpoint;

import com.hqy.cloud.actuator.core.Indicator;
import com.hqy.cloud.actuator.model.MicroServerSwitcherInfo;
import com.hqy.cloud.actuator.service.MicroServiceGradeManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 服务治理-开关升降级控制端点
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 16:49
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Endpoint(id = MicroServiceGradeSwitcherEndpoint.ID)
public class MicroServiceGradeSwitcherEndpoint implements Indicator<MicroServerSwitcherInfo> {
    public static final String ID = "switcher";
    private final MicroServiceGradeManageService microServiceGradeManageService;

    @Override
    public String indicatorId() {
        return ID;
    }

    @ReadOperation
    public Map<String, Object> readEndpoint() {
        return microServiceGradeManageService.getServerSwitcherInfo();
    }

    @WriteOperation
    public void writeEndpoint(Integer id, Object status) {
        MicroServerSwitcherInfo switcherInfo;
        if (status instanceof String) {
            switcherInfo = new MicroServerSwitcherInfo(id, Boolean.valueOf((String) status));
        } else {
            switcherInfo = new MicroServerSwitcherInfo(id, status);
        }
        microServiceGradeManageService.changeServerSwitcher(switcherInfo);
    }


}
