package com.hqy.cloud.id.component.segment.model;

import lombok.Data;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 15:16
 */
@Data
public class Segment {

    private AtomicLong value = new AtomicLong(0);
    private volatile long max;
    private volatile int step;
    private volatile int randomStep;
    private final SegmentBuffer buffer;

    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    public long getIdle() {
        return this.getMax() - getValue().get();
    }

    @Override
    public String toString() {
        return "Segment(" + "value:" +
                value +
                ",max:" +
                max +
                ",step:" +
                step +
                ")";
    }

}
