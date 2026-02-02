package com.hqy.cloud.communication.facade;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Validator;
import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.result.R;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.communication.constants.CommunicationResultCode;
import com.hqy.cloud.communication.request.PhoneMsgParams;
import com.hqy.cloud.communication.service.CommunicationFacadeService;
import com.hqy.cloud.communication.sms.core.SmsSender;
import com.hqy.cloud.infrastructure.random.RandomCodeScene;
import com.hqy.cloud.infrastructure.random.RandomCodeService;
import com.hqy.cloud.rpc.dubbo.DubboConstants;
import com.hqy.cloud.rpc.dubbo.facade.Facade;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.concurrent.TimeUnit;

/**
 * @author qiyuan.hong
 * @date 2024/7/11
 */
@RequiredArgsConstructor
@DubboService(version = DubboConstants.DEFAULT_DUBBO_SERVICE_VERSION)
public class CommunicationFacadeServiceImpl implements CommunicationFacadeService {
    private final RandomCodeService randomCodeService;
    private final SmsSender smsSender;

    @Facade
    @Override
    public R<Boolean> sendAuthSms(PhoneMsgParams params) {
        String phone = params.getPhone();
        Assert.isTrue(Validator.isMobile(phone), () -> new BizException(CommunicationResultCode.INVALID_PHONE));

        // 发送验证码
        String code = randomCodeService.randomNumber(
                6,
                params.getExpiredSeconds() == null ? 600 : params.getExpiredSeconds(),
                TimeUnit.SECONDS,
                RandomCodeScene.SMS_AUTH,
                params.getClientId(),
                phone);
        return smsSender.send(params.getSmsType().type, phone, code) ? R.ok() : R.failed(CommunicationResultCode.FAILED_SEND_SMS);
    }
}
