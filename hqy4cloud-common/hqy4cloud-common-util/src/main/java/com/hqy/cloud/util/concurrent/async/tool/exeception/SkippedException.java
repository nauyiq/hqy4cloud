package com.hqy.cloud.util.concurrent.async.tool.exeception;

/**
 * 如果任务在执行之前，自己后面的任务已经执行完或正在被执行，则抛该exception
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/11 14:14
 */
public class SkippedException extends RuntimeException {

    public SkippedException() {
        super();
    }

    public SkippedException(String message) {
        super(message);
    }
}
