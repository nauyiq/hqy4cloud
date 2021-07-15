package com.hqy.test.juc.cas;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author qy
 * @Create 2021/7/13 22:20
 */
public class CASDemo {

    //CAS compareAndSet 比较并交换
    public static void main(String[] args) {
        AtomicInteger integer = new AtomicInteger(2020);

        // =============中间线程操作=======================
        integer.compareAndSet(2020, 2021);
        integer.compareAndSet(2021, 2020);
        // =============中间线程操作=======================

        integer.compareAndSet(2020, 2021);

        System.out.println(integer.get());
    }


}
