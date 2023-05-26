package com.hqy.cloud.web.upload.support;

import com.google.common.util.concurrent.AbstractFuture;
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
    private final Object result;

    private FlexibleResultUploadResponse(UploadMode.Mode mode, Object result) {
        this.mode = mode;
        this.result = result;
    }

    public static UploadResponse of(UploadMode.Mode mode, Object result) {
        return new FlexibleResultUploadResponse(mode, result);
    }

    @Override
    public UploadMode.Mode uploadMode() {
        return mode;
    }

    @Override
    public UploadResult getResult() {
        if (uploadMode().equals(UploadMode.Mode.SYNC)) {
            return (UploadResult) result;
        }
        AsyncUploadFileCallFuture future = (AsyncUploadFileCallFuture) result;
        try {
            return future.get();
        } catch (Throwable cause) {
            // FAILED ASYNC GET. RETURN DEFAULT RESULT
            return UploadResult.ofTimeout();
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
