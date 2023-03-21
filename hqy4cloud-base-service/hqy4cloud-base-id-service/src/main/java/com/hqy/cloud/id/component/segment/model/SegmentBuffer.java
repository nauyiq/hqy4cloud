package com.hqy.cloud.id.component.segment.model;

import lombok.Data;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/3/21 15:17
 */
@Data
public class SegmentBuffer {

    private String key;

    /**
     * 双buffer
     */
    private final Segment[] segments;

    /**
     * 当前的使用的segment的index
     */
    private volatile int currentPos;

    /**
     * 下一个segment是否处于可切换状态
     */
    private volatile boolean nextReady;

    /**
     * 是否初始化完成
     */
    private volatile boolean initOk;

    /**
     * 线程是否在运行中
     */
    private final AtomicBoolean threadRunning;

    private volatile int step;
    private volatile int minStep;
    private volatile long updateTimestamp;

    private final ReadWriteLock lock;

    public SegmentBuffer() {
        segments = new Segment[] { new Segment(this), new Segment(this) };
        currentPos = 0;
        nextReady = false;
        initOk = false;
        threadRunning = new AtomicBoolean(false);
        lock = new ReentrantReadWriteLock();
    }

    public Segment getCurrent() {
        return segments[currentPos];
    }

    public Lock rLock() {
        return lock.readLock();
    }

    public Lock wLock() {
        return lock.writeLock();
    }

    public int nextPos() {
        return (currentPos + 1) % 2;
    }

    public void switchPos() {
        currentPos = nextPos();
    }

    @Override
    public String toString() {
        return "SegmentBuffer{" +
                "key='" + key + '\'' +
                ", segments=" + Arrays.toString(segments) +
                ", currentPos=" + currentPos +
                ", nextReady=" + nextReady +
                ", initOk=" + initOk +
                ", threadRunning=" + threadRunning +
                ", step=" + step +
                ", minStep=" + minStep +
                ", updateTimestamp=" + updateTimestamp +
                '}';
    }


}
