package com.hqy.cloud.file.check.support;

import com.hqy.cloud.file.check.FileUploadChecker;
import com.hqy.cloud.file.common.FileException;
import com.hqy.cloud.file.common.request.BaseFileUploadRequest;
import com.hqy.cloud.file.common.result.FileResultCode;
import com.hqy.cloud.file.common.utils.FileUtil;

/**
 * 文件类型校验
 * @author hongqy
 * @date 2025/5/6
 */
public class FileUploadTypeChecker implements FileUploadChecker {
    @Override
    public void check(BaseFileUploadRequest baseFileUploadRequest) throws FileException {
        String filename = baseFileUploadRequest.getFilename();
        if (!FileUtil.validateFileType(filename)) {
            throw new FileException(FileResultCode.NOT_SUPPORT_FILE_TYPE);
        }
    }
}
