package com.hqy.cloud.file.common;

import com.hqy.cloud.common.base.exception.BizException;
import com.hqy.cloud.common.result.Result;

/**
 * @author hongqy
 * @date 2025/4/3
 */
public class FileException extends BizException {

    public FileException(String code) {
        super(code);
    }

    public FileException(String code, String message) {
        super(code, message);
    }

    public FileException(Result result) {
        super(result);
    }

    public FileException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
