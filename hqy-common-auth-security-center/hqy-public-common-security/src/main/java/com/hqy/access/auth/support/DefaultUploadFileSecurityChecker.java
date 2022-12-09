package com.hqy.access.auth.support;

import cn.hutool.http.ContentType;
import com.hqy.access.auth.UploadFileSecurityChecker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * @see com.hqy.access.auth.UploadFileSecurityChecker
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/6 9:49
 */
public class DefaultUploadFileSecurityChecker implements UploadFileSecurityChecker {

    @Override
    public boolean isUploadFileRequest(String accessContentType, String accessUri) {
        if (StringUtils.isAnyBlank(accessContentType, accessUri)) {
            return false;
        }
        return accessContentType.equals(ContentType.MULTIPART.getValue()) && EndpointAuthorizationManager.getInstance().isUploadFileRequest(accessUri);
    }

    @Override
    public boolean checkFileSecurity(MultipartFile file) {
        //TODO.
        return true;
    }
}
