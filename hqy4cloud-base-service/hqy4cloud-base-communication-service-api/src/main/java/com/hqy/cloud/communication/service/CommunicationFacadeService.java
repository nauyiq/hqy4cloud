package com.hqy.cloud.communication.service;

import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.communication.request.PhoneMsgParams;

/**
 * @author qiyuan.hong
 * @date 2024/7/11
 */
public interface CommunicationFacadeService {

    /**
     * 发送认证短信
     * @param params 请求参数
     * @return       是否发送成功
     */
    R<Boolean> sendAuthSms(PhoneMsgParams params);

}
