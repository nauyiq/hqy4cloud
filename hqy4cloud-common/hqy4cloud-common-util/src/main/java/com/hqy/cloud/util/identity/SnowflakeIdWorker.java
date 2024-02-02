package com.hqy.cloud.util.identity;

import com.google.common.base.Preconditions;
import com.hqy.cloud.util.IpUtil;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;
import java.util.Random;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/4/22
 */
@Slf4j
public class SnowflakeIdWorker {

    private static final Random RANDOM = new Random();

    /**
     * 起始的时间戳
     */
    private final long twepoch;

    /**
     * 机器id所占的位数
     */
    private final long workerIdBits = 10L;


    private final long maxWorkerId = ~(-1L << workerIdBits);

    private final long sequenceBits = 12L;

    private final long workerIdShift = sequenceBits;

    private final long timestampLeftShift = sequenceBits + workerIdBits;

    private final long sequenceMask = ~(-1L << sequenceBits);

    /**
     * 工作机器ID(0~31)
     */
    private final long workerId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成ID的时间截
     */
    private long lastTimestamp = -1L;



    public SnowflakeIdWorker() {
        long pid = 0;
        try {
            pid = Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
            log.info("Progress ID: " + pid);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (pid > maxWorkerId) {
            pid = pid % maxWorkerId;
        }
        this.workerId = pid;
        //Thu Nov 04 2010 09:42:54 GMT+0800 (中国标准时间)
        this.twepoch = 1288834974657L;
    }

    public SnowflakeIdWorker(long workerId) {
        //Thu Nov 04 2010 09:42:54 GMT+0800 (中国标准时间)
        this(workerId, 1288834974657L);
    }


    /**
     * 构造函数
     * @param workerId     工作ID (0~31)
     * @param twepoch      起始的时间戳
     */
    public SnowflakeIdWorker(long workerId, long twepoch) {
        this.twepoch = twepoch;
        Preconditions.checkArgument(timeGen() > twepoch, "Snowflake not support twepoch gt currentTime");
        Preconditions.checkArgument(workerId >= 0 && workerId <= maxWorkerId, "workerID must gte 0 and lte 1023");
        this.workerId = workerId;
    }


    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();
        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
        if (timestamp < lastTimestamp) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            long offset = lastTimestamp - timestamp;
            if (offset <= 5) {
                try {
                    wait(offset << 1);
                    timestamp = timeGen();
                    if (timestamp < lastTimestamp) {
                       throw illegalArgumentException;
                    }
                } catch (InterruptedException e) {
                    log.error("wait interrupted");
                    throw illegalArgumentException;
                }
            } else {
                throw illegalArgumentException;
            }
        }

        //如果是同一时间生成的，则进行毫秒内序列 序列在id中占的位数
        if (lastTimestamp == timestamp) {
             //生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                //seq 为0的时候表示是下一毫秒时间开始对seq做随机
                sequence = RANDOM.nextInt(100);
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //如果是新的ms开始
            sequence = RANDOM.nextInt(100);
        }
        //上次生成ID的时间截
        lastTimestamp = timestamp;
        return ((timestamp - twepoch) << timestampLeftShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }



}
