package com.hqy.cloud.communication.service;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.communication.request.PhoneMsgParams;
import com.hqy.cloud.rpc.dubbo.DubboConstants;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author qiyuan.hong
 * @date 2024/7/11
 */
@DubboService(version = DubboConstants.DEFAULT_DUBBO_SERVICE_VERSION)
public class CommunicationFacadeServiceImpl implements CommunicationFacadeService{


    @Override
    public R<Boolean> sendSms(PhoneMsgParams params) {
        return null;
    }
}
