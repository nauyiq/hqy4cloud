package com.hqy.rpc.registry.api.support;

/**
 * Wrapper Exception, it is used to indicate that {@link FailBackRegistry} skips FailBack.
 * <p>
 * NOTE: Expect to find other more conventional ways of instruction.
 *
 * @see FailBackRegistry
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/6/27 14:43
 */
public class SkipFailBackWrapperException extends RuntimeException {

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
