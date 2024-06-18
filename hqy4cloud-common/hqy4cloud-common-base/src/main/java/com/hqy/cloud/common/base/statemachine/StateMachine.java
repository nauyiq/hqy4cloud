package com.hqy.cloud.common.base.statemachine;

/**
 * 状态机
 * @author qiyuan.hong
 * @date 2024/7/22
 */
public interface StateMachine<STATE, EVENT> {

    /**
     * 状态转换
     * @param state 状态
     * @param event 事件
     * @return      转换的状态
     */
    public STATE transition(STATE state, EVENT event);

}
