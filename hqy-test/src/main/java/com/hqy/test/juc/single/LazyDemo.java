package com.hqy.test.juc.single;

import java.util.Objects;

/**
 * DCL懒汉式
 * 双重检测锁模式
 * @Author qy
 * @Create 2021/7/13 21:52
 */
public class LazyDemo {

    private LazyDemo(){};


    private volatile static LazyDemo instance = null;


    public static LazyDemo getInstance() {

        if (instance == null) {
            synchronized (LazyDemo.class) {
                if (Objects.isNull(instance)) {
                    instance = new LazyDemo();
                }
            }
        }
        return instance;

    }


}
