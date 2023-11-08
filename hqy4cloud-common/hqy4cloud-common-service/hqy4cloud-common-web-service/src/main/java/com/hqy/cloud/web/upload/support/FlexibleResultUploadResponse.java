package com.hqy.cloud.web.upload.support;

import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.ListenableFuture;
import com.hqy.cloud.web.common.UploadResult;
import com.hqy.cloud.web.common.annotation.UploadMode;
import com.hqy.cloud.web.upload.UploadResponse;

import javax.annotation.Nullable;

/**
 * FlexibleUploadResponse.
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/25 15:10
 */
public class FlexibleResultUploadResponse implements UploadResponse {

    private final UploadMode.Mode mode;
    private final UploadResult result;
    private final ListenableFuture<UploadResult> future;

    private FlexibleResultUploadResponse(UploadMode.Mode mode, UploadResult result, ListenableFuture<UploadResult> future) {
        this.mode = mode;
        this.result = result;
        this.future = future;
    }

    public static UploadResponse of(UploadMode.Mode mode, UploadResult result) {
        return new FlexibleResultUploadResponse(mode, result, null);
    }

    public static UploadResponse of(UploadMode.Mode mode, UploadResult result, ListenableFuture<UploadResult> future) {
        return new FlexibleResultUploadResponse(mode, result, future);
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
        if (uploadMode() == UploadMode.Mode.ONEWAY || !syncWait) {
            return result;
        } else {
            try {
                return future.get();
            } catch (Throwable cause) {
                // FAILED ASYNC GET. RETURN DEFAULT RESULT
                return UploadResult.ofTimeout();
            }
        }
    }

    public static class AsyncUploadFileCallFuture extends AbstractFuture<UploadResult> {

        public static AsyncUploadFileCallFuture create() {
            return new AsyncUploadFileCallFuture();
        }

        private AsyncUploadFileCallFuture() {
        }

        @Override
        public boolean set(@Nullable UploadResult value) {
            return super.set(value);
        }

        @Override
        public boolean setException(Throwable throwable) {
            return super.setException(throwable);
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            // Async call futures represent requests running on some other service,
            // there is no way to cancel the request once it has been sent.
            return false;
        }
    }


}
