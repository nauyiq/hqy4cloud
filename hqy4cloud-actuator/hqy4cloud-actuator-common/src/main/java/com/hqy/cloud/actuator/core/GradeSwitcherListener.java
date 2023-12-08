package com.hqy.cloud.actuator.core;

import com.hqy.cloud.actuator.model.MicroServerSwitcherInfo;

import java.util.Collections;
import java.util.Set;

/**
 * GradeSwitcherListener
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/11/20 10:42
 */
public interface GradeSwitcherListener {

    /**
     * return support switcher ids.
     * @return switcher ids
     */
    default Set<Integer> supportSwitchers() {
        return Collections.emptySet();
    }

    /**
     * do something on switcher grade change.
     * @param info {@link MicroServerSwitcherInfo}
     */
    void onGradeChange(MicroServerSwitcherInfo info);

}
