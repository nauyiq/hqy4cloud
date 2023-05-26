package com.hqy.cloud.web.upload.support;

import com.hqy.cloud.web.common.annotation.UploadMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件上传上下文
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 14:57
 */
public class UploadContext {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadState {
        private UploadMode.Mode mode;
        private boolean copyFileContent;
    }

    private static final ThreadLocal<UploadState> UPLOAD_MODE_STATE = new ThreadLocal<>();

    public static void setMode(UploadState state) {
        UPLOAD_MODE_STATE.set(state);
    }

    public static UploadState getState() {
        return UPLOAD_MODE_STATE.get();
    }

    public static void removeMode() {
        UPLOAD_MODE_STATE.remove();
    }












}
