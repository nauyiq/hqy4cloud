package com.hqy.cloud.web.upload.support;

import com.hqy.cloud.web.common.annotation.UploadMode;

/**
 * 文件上传上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 14:57
 */
public class UploadContext {
    private static final ThreadLocal<UploadMode.Mode> UPLOAD_MODE_STATE = new ThreadLocal<>();

    public static void setMode(UploadMode.Mode mode) {
        UPLOAD_MODE_STATE.set(mode);
    }

    public static UploadMode.Mode getMode() {
        return UPLOAD_MODE_STATE.get();
    }

    public static void removeMode() {
        UPLOAD_MODE_STATE.remove();
    }












}
