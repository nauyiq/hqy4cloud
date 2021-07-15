package com.hqy.test.juc.single;

/**
 * enum本身就是一个类 本身也是一个class类
 * @Author qy
 * @Create 2021/7/13 22:07
 */
public enum LazyEnum {

    INSTANCE;


    public LazyEnum getInstance() {
        return INSTANCE;
    }


}
