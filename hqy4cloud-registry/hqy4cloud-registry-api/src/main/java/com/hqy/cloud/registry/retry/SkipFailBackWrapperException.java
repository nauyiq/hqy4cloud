package com.hqy.cloud.registry.retry;

import com.hqy.cloud.registry.api.FailedBackRegistry;

import java.io.Serial;

/**
 * Wrapper Exception, it is used to indicate that {@link FailedBackRegistry} skips FailBack.
 * <p>
 * NOTE: Expect to find other more conventional ways of instruction.
 * @author qiyuan.hong
 * @version 1.0
 */
public class SkipFailBackWrapperException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8726228659908278616L;

    public SkipFailBackWrapperException(Throwable cause) {
        super(cause);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        // do nothing
        return null;
    }
}
