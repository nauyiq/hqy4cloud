package com.hqy.cloud.auth.core.authentication;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传安全检察员.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/12/6 9:32
 */
public interface UploadFileSecurityChecker {

    /**
     * 判断请求是否是文件上传.
     * @param accessContentType 请求的content-type
     * @param accessUri         访问uri
     * @return                  result.
     */
    boolean isUploadFileRequest(String accessContentType, String accessUri);


    /**
     * 检查文件是否安全的
     * @param file  {@link MultipartFile}
     * @return      result.
     */
    boolean checkFileSecurity(MultipartFile file);

}
