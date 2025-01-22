package com.hqy.cloud.common.base.statemachine;

import com.google.common.base.Joiner;
import com.hqy.cloud.common.base.lang.StringConstants;
import org.springframework.util.Assert;

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
        return STATE_TRANSITION_MAP.get(Joiner.on(StringConstants.Symbol.COLON).join(state, event));
    }

    protected void putTransition(STATE origin, EVENT event, STATE target) {
        Assert.notNull(target, "Target status should not be null.");
        STATE_TRANSITION_MAP.put(Joiner.on(StringConstants.Symbol.COLON).join(origin, event), target);
    }

}
