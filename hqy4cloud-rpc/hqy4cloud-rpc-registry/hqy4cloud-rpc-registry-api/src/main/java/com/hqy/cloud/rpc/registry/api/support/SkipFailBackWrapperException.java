package com.hqy.cloud.rpc.registry.api.support;

import java.io.Serial;

/**
 * Wrapper Exception, it is used to indicate that {@link FailBackRPCRegistry} skips FailBack.
 * <p>
 * NOTE: Expect to find other more conventional ways of instruction.
 * @see FailBackRPCRegistry
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 14:43
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
