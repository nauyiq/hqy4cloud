package com.hqy.cloud.gateway.server.auth;

import com.hqy.cloud.gateway.server.support.ImageCodeServer;
import com.hqy.cloud.gateway.server.AbstractCodeAuthorizationChecker;
import org.springframework.stereotype.Component;


/**
 * 图片二维码检查员.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/21 16:54
 */
@Component
public class ImageCodeAuthorizationChecker extends AbstractCodeAuthorizationChecker {

    public ImageCodeAuthorizationChecker(ImageCodeServer imageCodeHandler) {
        super(imageCodeHandler);
    }
}
