package com.hqy.cloud.communication.sms.core.support;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.google.common.collect.Maps;
import com.hqy.cloud.common.result.ResultCode;
import com.hqy.cloud.communication.sms.common.SmsConstants;
import com.hqy.cloud.communication.sms.common.SmsException;
import com.hqy.cloud.communication.sms.config.SmsProperties;
import com.hqy.cloud.communication.sms.core.SmsSender;
import com.hqy.cloud.lock.annotation.DistributeLock;
import com.hqy.cloud.util.HttpRestUtil;
import io.netty.handler.codec.http.HttpHeaderNames;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * sms发送器
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/7/12
 */
@Slf4j
@RequiredArgsConstructor
public class DefaultSmsSender implements SmsSender {
    private final SmsProperties smsProperties;

    @Override
    @DistributeLock(scene = "SEND_SMS", keyExpression = "#phoneNumber")
    public boolean send(String type, String phoneNumber, String code) {
        SmsProperties.SmsTemplate template = smsProperties.getTemplates().get(type);
        if (template == null) {
            throw new SmsException(ResultCode.NOT_FOUND_SMS_TEMPLATE.getCode(), "Not found sms template by type: " + type);
        }

        // 创建国阳云HTTP URL请求 参考文档：http://help.guoyangyun.com/Problem/Qm.html
        // 请求参数
        Map<String, String> params = Maps.newHashMapWithExpectedSize(4);
        params.put("mobile", phoneNumber);
        params.put("param", "**code**:" + code + ",**minute**:5");
        params.put("smsSignId", template.getSmsSignId());
        params.put("templateId", template.getTemplateId());

        HttpRequest request = HttpRequest.post(HttpRestUtil.buildUrl(smsProperties.getHost(), smsProperties.getPath(), params))
                .header(HttpHeaderNames.AUTHORIZATION.toString(), SmsConstants.APP_CODE + smsProperties.getAppcode());

        try {
            // 调用API
            HttpResponse response = request.execute();
            return response.isOk();
        } catch (Throwable cause) {
            log.error("Failed execute to send sms message", cause);
            return false;
        }
    }






}
