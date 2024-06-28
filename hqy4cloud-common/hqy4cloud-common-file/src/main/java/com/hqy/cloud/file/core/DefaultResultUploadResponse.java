package com.hqy.cloud.file.core;


import com.hqy.cloud.file.common.annotation.UploadMode;
import com.hqy.cloud.file.common.result.UploadResponse;
import com.hqy.cloud.file.common.result.UploadResult;
import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * FlexibleUploadResponse.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 15:10
 */
@Getter
public class DefaultResultUploadResponse implements UploadResponse {

    private final UploadMode.Mode mode;
    private final UploadResult result;
    private final CompletableFuture<UploadResult> future;

    private DefaultResultUploadResponse(UploadMode.Mode mode, UploadResult result, CompletableFuture<UploadResult> future) {
        this.mode = mode;
        this.result = result;
        this.future = future;
    }

    public static UploadResponse of(UploadMode.Mode mode, UploadResult result) {
        return new DefaultResultUploadResponse(mode, result, null);
    }

    public static UploadResponse of(UploadMode.Mode mode, UploadResult result, CompletableFuture<UploadResult> future) {
        return new DefaultResultUploadResponse(mode, result, future);
    }

    @Override
    public UploadMode.Mode uploadMode() {
        return mode;
    }

    @Override
    public UploadResult getResult(boolean syncWait) {
        // 同步调用直接返回结果即可
        if (uploadMode() == UploadMode.Mode.SYNC) {
            return result;
        }
        // oneway 同理返回结果, 不管syncWait是否等待同步结果
        if (uploadMode() == UploadMode.Mode.ONEWAY) {
            return result;
        }

        if (!syncWait) {
            // 不等待则直接返回result
            return result;
        }

        try {
            return future.get();
        } catch (Throwable cause) {
            // FAILED ASYNC GET. RETURN DEFAULT RESULT
            return UploadResult.ofTimeout();
        }
    }

}
