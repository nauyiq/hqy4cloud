package com.hqy.fundation.common.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 线程安全的队列（内部依赖了ConcurrentLinkedQueue ，而不是Linked list）
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/28 13:38
 */
@Slf4j
public class SimpleMessageQueue<T> {

    private int theMaxSizeOfQueue = Short.MAX_VALUE;

    private final ConcurrentLinkedQueue<T> msgQueue = new ConcurrentLinkedQueue<T>();


    public SimpleMessageQueue() {
    }

    public SimpleMessageQueue(int maxSize) {
        this.theMaxSizeOfQueue = maxSize;
    }

    /**
     * @return 队列是否为空
     */
    public boolean isEmpty() {
        return this.msgQueue.isEmpty();
    }

    /**
     * @return 取队列头 如果当前队列为空 则为null
     */
    public T takeHead() {
        T t = null;
        try {
            if (!this.msgQueue.isEmpty()) {
                t = this.msgQueue.poll();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return t;
    }

    /**
     * 向队列的尾巴添加一个元素，注意 本方法不总是成功的！
     * @param t element
     * @return true-添加成功； false－队列已满（逻辑上已经满了），需等候
     */
    public boolean addTail(T t) {
        if (Objects.isNull(t)) {
            return false;
        }
        try {
            if (this.msgQueue.size() >= this.theMaxSizeOfQueue) {
                log.warn("@@@ Current Queue is full, please check and retry later. maxSize:{}", theMaxSizeOfQueue);
                return false;
            } else {
                msgQueue.add(t);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }


    public int size() {
        return this.msgQueue.size();
    }

    public void clear() {
        this.msgQueue.clear();
    }




}
