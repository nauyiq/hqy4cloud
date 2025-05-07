package com.hqy.cloud.file.check;

import com.hqy.cloud.file.common.FileException;
import com.hqy.cloud.file.common.request.BaseFileUploadRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件上传校验调用链
 * @author hongqy
 * @date 2025/5/6
 */
public class FileUploadCheckerChain implements FileUploadChecker {
    private final List<FileUploadChecker> checkers = new ArrayList<>();

    @Override
    public void check(BaseFileUploadRequest baseFileUploadRequest) throws FileException {
        checkers.forEach(checker -> checker.check(baseFileUploadRequest));
    }

    public FileUploadCheckerChain addChecker(FileUploadChecker checker) {
        checkers.add(checker);
        return this;
    }

}
