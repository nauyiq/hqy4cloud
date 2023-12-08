package com.hqy.cloud.actuator.server;

import com.hqy.cloud.actuator.core.GradeSwitcherListener;
import com.hqy.cloud.actuator.model.MicroServerSwitcherInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 10:45
 */
@Slf4j
public class DefaultGradeSwitcherListener implements GradeSwitcherListener {

    @Override
    public void onGradeChange(MicroServerSwitcherInfo info) {
        GradeSwitcherListener listener = info.getListener();
        if (listener != null) {
            log.info("Do extension grade switcher listener.");
            listener.onGradeChange(info);
        }
    }
}
