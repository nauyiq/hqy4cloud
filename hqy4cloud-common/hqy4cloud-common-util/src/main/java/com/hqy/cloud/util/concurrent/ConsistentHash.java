package com.hqy.cloud.util.concurrent;

import com.hqy.cloud.util.AssertUtil;
import com.hqy.cloud.util.MathUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * 一致性hash, 优化hash算法，可以保证数据的离散性和单一性。
 * @author qiyuan.hong
 * @version 1.0
 * @date 2024/1/29
 */
@Slf4j
public class ConsistentHash<T> {
    public static final int DEFAULT_REPLICAS = 100;

    /**
     * hash方法, 默认使用改进的32位FNV算法
     */
    private final Function<String, Integer> hashFunction;

    /**
     * 复制节点的个数
     */
    private final int replicas;

    /**
     * 一致性Hash环
     */
    private final SortedMap<Integer, T> circle = new TreeMap<>();

    /**
     * 读写锁
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    public ConsistentHash(List<T> nodes) {
        this(DEFAULT_REPLICAS, nodes);
    }

    public ConsistentHash(int replicas, List<T> nodes) {
        this(MathUtil::fnvHash, replicas, nodes);
    }

    public ConsistentHash(Function<String, Integer> hashFunction, int replicas, List<T> nodes) {
        this.hashFunction = hashFunction;
        this.replicas = replicas;
        refresh(nodes);
    }

    /**
     * 刷新hash环上的节点.
     * @param nodes 节点列表.
     */
    public void refresh(List<T> nodes) {
        readWriteLock.writeLock().lock();
        try {
            Collection<T> allNodes = circle.values();
            if (CollectionUtils.isEmpty(nodes)) {
                log.warn("Refresh node is empty, will clear hash circle.");
                allNodes.clear();
            } else {
                if (CollectionUtils.isNotEmpty(allNodes)) {
                    // clear old nodes.
                    allNodes.clear();
                }
                // add node
                nodes.forEach(this::add);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    /**
     * 刷新hash环上的节点 并且通过key获取一致性hash环中最近的顺时针节点
     * @param key   请求的key, 取得顺时针方向上最近的一个虚拟节点对应的实际节点
     * @param nodes 节点列表.
     * @return      节点
     */
    public T refreshAndGet(Object key, List<T> nodes) {
        // 获取写锁
        readWriteLock.writeLock().lock();
        try {
            Collection<T> allNodes = circle.values();
            if (CollectionUtils.isEmpty(nodes)) {
                log.warn("Refresh node is empty, will clear hash circle.");
                allNodes.clear();
            } else {
                if (CollectionUtils.isNotEmpty(allNodes)) {
                    // clear old nodes.
                    allNodes.clear();
                }
                // add node
                nodes.forEach(this::add);
            }
            // 获取数据 锁降级
            return get(key);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }



    /**
     * 获取一致性hash环中最近的顺时针节点
     * @param key 请求的key, 取得顺时针方向上最近的一个虚拟节点对应的实际节点
     * @return    节点对象
     */
    public T get(Object key) {
        AssertUtil.notNull(key, "Hash key should not be null.");
        int hash = hash(key, 0);
        readWriteLock.readLock().lock();
        try {
            if (circle.isEmpty()) {
                return null;
            }
            if (!circle.containsKey(hash)) {
                //返回此映射的部分视图，其键大于等于hash
                SortedMap<Integer, T> map = circle.tailMap(hash);
                hash = map.isEmpty() ? circle.firstKey() : map.firstKey();
            }
            return circle.get(hash);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }


    /**
     * 新增hash节点 <br/>
     * 每增加一个节点，就会在闭环上增加给定复制节点数
     * @param node 节点对象.
     */
    public void add(T node) {
        for (int i = 0; i < replicas; i++) {
            circle.put(hash(node, i), node);
        }
    }

    /**
     * 移除hash节点 <br/>
     * 移除节点的同时移除相应的虚拟节点
     * @param node 节点对象.
     */
    public void remove(T node) {
        for (int i = 0; i < replicas; i++) {
            circle.remove(hash(node, i));
        }
    }


    public Function<String, Integer> getHashFunction() {
        return hashFunction;
    }

    private int hash(Object key, int i) {
        return hashFunction.apply((key.toString() + i));
    }


    public static void main(String[] args) {
//        List<String> address = List.of("192.168.0.2:9091", "192.168.0.3:9091", "192.168.0.4:9091", "192.168.0.5:9091");
        List<String> address = List.of("192.168.0.2:9091", "192.168.0.3:9091", "192.168.0.4:9091");
        ConsistentHash<String> consistentHash = new ConsistentHash<>(address);

        System.out.println(consistentHash.get("111111"));
        System.out.println(consistentHash.get("222222"));
        System.out.println(consistentHash.get("333333"));
        System.out.println(consistentHash.get("444444"));
        System.out.println(consistentHash.get("555555"));
        System.out.println(consistentHash.get("666666"));
        System.out.println(consistentHash.get("777777"));
        System.out.println(consistentHash.get("888888"));
        System.out.println(consistentHash.get("999999"));
        System.out.println(consistentHash.get("000000"));


        System.out.println("======================================");
        consistentHash.remove("192.168.0.2:9091");
        System.out.println(consistentHash.get("111111"));
        System.out.println(consistentHash.get("222222"));
        System.out.println(consistentHash.get("333333"));
        System.out.println(consistentHash.get("444444"));
        System.out.println(consistentHash.get("555555"));
        System.out.println(consistentHash.get("666666"));
        System.out.println(consistentHash.get("777777"));
        System.out.println(consistentHash.get("888888"));
        System.out.println(consistentHash.get("999999"));
        System.out.println(consistentHash.get("000000"));

    }


}
