package com.hqy.cloud.communication.facade;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.communication.request.PhoneMsgParams;
import com.hqy.cloud.communication.service.CommunicationFacadeService;
import com.hqy.cloud.communication.sms.core.SmsSender;
import com.hqy.cloud.communication.sms.core.support.DefaultSmsSender;
import com.hqy.cloud.rpc.dubbo.DubboConstants;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author qiyuan.hong
 * @date 2024/7/11
 */
@RequiredArgsConstructor
@DubboService(version = DubboConstants.DEFAULT_DUBBO_SERVICE_VERSION)
public class CommunicationFacadeServiceImpl implements CommunicationFacadeService {
    private final SmsSender smsSender;

    @Override
    public R<Boolean> sendAuthSms(PhoneMsgParams params) {
        return smsSender.send(params.getSmsType().type, params.getPhone(), params.getCode()) ? R.ok() : R.failed(ResultCode.FAILED_SEND_SMS);
    }
}
