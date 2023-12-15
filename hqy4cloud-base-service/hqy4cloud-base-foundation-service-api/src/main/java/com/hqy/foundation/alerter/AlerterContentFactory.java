package com.hqy.foundation.alerter;

/**
 * 报警内容工厂类
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/12/15 17:27
 */
public interface AlerterContentFactory<T, V> {

    /**
     * 创建业务报警内容对象
     * @param params 构造参数
     * @return       报警内容
     */
    V create(T params);

}
