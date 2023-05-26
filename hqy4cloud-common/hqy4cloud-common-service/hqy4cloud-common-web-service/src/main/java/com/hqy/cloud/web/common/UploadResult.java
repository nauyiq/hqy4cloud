package com.hqy.cloud.web.common;

import cn.hutool.core.util.StrUtil;
import com.hqy.cloud.common.result.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 上传结果对象
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 15:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadResult {
    private static final UploadResult DEFAULT_ERROR_RESULT = new UploadResult(false, "Failed execute to async get result timeout.", StrUtil.EMPTY, StrUtil.EMPTY);

    public static UploadResult success() {
        return UploadResult.of(StrUtil.EMPTY, StrUtil.EMPTY);
    }

    public static UploadResult failed(String message) {
        return new UploadResult(false, message, StrUtil.EMPTY, StrUtil.EMPTY);
    }

    public static UploadResult of(String relativePath, String path) {
        return new UploadResult(true, ResultCode.SUCCESS.message, relativePath, path);
    }


    /**
     * 是否上传成功.
     */
    private boolean result;

    /**
     * 上传结果消息
     */
    private String message;

    /**
     * 上传文件的相对路径
     */
    private String relativePath;

    /**
     * 上传文件的访问路径
     */
    private String path;

    public static UploadResult ofTimeout() {
        return DEFAULT_ERROR_RESULT;
    }


}
