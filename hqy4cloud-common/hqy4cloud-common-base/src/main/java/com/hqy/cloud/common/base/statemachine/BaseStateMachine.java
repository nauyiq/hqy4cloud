package com.hqy.cloud.common.base.statemachine;

import com.google.common.base.Joiner;
import com.hqy.cloud.common.base.lang.StringConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * @author qiyuan.hong
 * @date 2024/7/22
 */
public class BaseStateMachine<STATE, EVENT> implements StateMachine<STATE, EVENT> {
    private final Map<String, STATE> STATE_TRANSITION_MAP = new HashMap<>(8);

    @Override
    public STATE transition(STATE state, EVENT event) {
        return null;
    }

    protected void putTransition(STATE origin, EVENT event, STATE target) {
        STATE_TRANSITION_MAP.put(Joiner.on(StringConstants.Symbol.COLON).join(origin, event), target);
    }

}
