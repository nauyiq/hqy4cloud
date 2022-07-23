package com.hqy.rpc.api;

import java.io.Serializable;
import java.util.Map;

/**
 * rpc invoker result.
 * @see Invoker#invoke(Invocation)
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/8 17:36
 */
public interface Result extends Serializable {

    /**
     * Get invoke result.
     * @return result. if no result return null.
     */
    Object getValue();

    /**
     * Get exception.
     * @return @return exception. if no exception return null.
     */
    Throwable getException();

    /**
     * Has exception.
     * @return has exception.
     */
    boolean hasException();

    /**
     * Recreate.
     * <p>
     * <code>
     * if (hasException()) {
     * throw getException();
     * } else {
     * return getValue();
     * }
     * </code>
     *
     * @return result.
     * @throws Throwable if has exception throw it.
     */
    Object recreate() throws Throwable;

    /**
     * get parameters
     * @return get parameters
     */
    Map<String, String> getParameters();



}
