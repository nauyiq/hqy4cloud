package com.hqy.cloud.foundation.limiter;

import lombok.Data;

/**
 * redis流量限流器的检测结果
 * @author qy
 * @date 2021-08-04 15:04
 */
@Data
public class FlowResult {

    /**
     * 是否超限
     */
    private boolean overLimit;

    /**
     * 是否封禁
     */
    private boolean block;

    /**
     * 封禁时间
     */
    private int blockSeconds = Measurement.Seconds.ONE_SECONDS.seconds * 2;


    public FlowResult(boolean overLimit, boolean block) {
        this.overLimit = overLimit;
        this.block = block;
    }

    public FlowResult(boolean overLimit, boolean block, int blockSeconds) {
        this.overLimit = overLimit;
        this.block = block;
        this.blockSeconds = blockSeconds;
    }


    public static FlowResult build() {
        return new FlowResult(false, false);
    }

    public static FlowResult buildLimit() {
        return new FlowResult(true, false);
    }

    public static FlowResult buildBlock() {
        return new FlowResult(true, true);
    }

    public static FlowResult buildBlock(int blockSeconds) {
        return new FlowResult(true, true, blockSeconds);
    }


}
