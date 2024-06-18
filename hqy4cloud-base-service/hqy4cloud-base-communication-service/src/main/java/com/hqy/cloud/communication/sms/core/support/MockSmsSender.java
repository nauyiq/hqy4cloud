package com.hqy.cloud.communication.sms.core.support;

import com.hqy.cloud.communication.sms.core.SmsSender;
import com.hqy.cloud.lock.annotation.DistributeLock;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */

public class MockSmsSender implements SmsSender {

    @Override
    @DistributeLock(scene = "SEND_SMS", keyExpression = "#phoneNumber")
    public boolean send(String type, String phoneNumber, String code) {
        return true;
    }
}
