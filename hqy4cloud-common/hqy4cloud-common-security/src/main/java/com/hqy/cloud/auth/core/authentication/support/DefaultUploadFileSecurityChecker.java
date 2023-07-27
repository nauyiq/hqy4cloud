package com.hqy.cloud.auth.core.authentication.support;

import com.hqy.cloud.auth.core.authentication.UploadFileSecurityChecker;
import com.hqy.cloud.auth.core.component.EndpointAuthorizationManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

/**
 * @see UploadFileSecurityChecker
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
        return accessContentType.equals(MediaType.MULTIPART_FORM_DATA_VALUE) && EndpointAuthorizationManager.getInstance().isUploadFileRequest(accessUri);
    }

    @Override
    public boolean checkFileSecurity(MultipartFile file) {
        //TODO.
        return true;
    }
}
