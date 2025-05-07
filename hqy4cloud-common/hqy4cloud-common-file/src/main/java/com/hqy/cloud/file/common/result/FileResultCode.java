package com.hqy.cloud.file.common.result;

import com.hqy.cloud.common.result.Result;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author hongqy
 * @date 2025/4/3
 */
@Getter
@AllArgsConstructor
public enum FileResultCode implements Result {

    FILE_IS_EMPTY("FILE_IS_EMPTY", "不能上传空文件"),

    NOT_SUPPORT_FILE_TYPE("NOT_SUPPORT_FILE_TYPE", "不支持的文件类型"),

    UPLOAD_FAILED("UPLOAD_FAILED", "文件上传失败"),

    UPLOAD_EXCEPTION("UPLOAD_EXCEPTION", "文件上传异常"),

    DOWNLOAD_FAILED("DOWNLOAD_FAILED", "文件下载失败"),
    ;

    public final String code;
    public final String message;
}
