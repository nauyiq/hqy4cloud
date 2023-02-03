package com.hqy.mq.rocketmq.demo;

import com.hqy.util.identity.ProjectSnowflakeIdWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/5/18 9:35
 */
public class OrderStep {

    public Long id;

    public String describe;

    public OrderStep() {
    }

    public OrderStep(Long id, String describe) {
        this.id = id;
        this.describe = describe;
    }

    @Override
    public String toString() {
        return "OrderStep{" +
                "id=" + id +
                ", describe='" + describe + '\'' +
                '}';
    }


    public static List<OrderStep> orderSteps() {
        List<OrderStep> orderStepList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            long orderId = ProjectSnowflakeIdWorker.getInstance().nextId();
            orderStepList.add(new OrderStep(orderId, "创建"));
            orderStepList.add(new OrderStep(orderId, "付款"));
            orderStepList.add(new OrderStep(orderId, "推送"));
            orderStepList.add(new OrderStep(orderId, "完成"));
        }
        return orderStepList;
    }


}
