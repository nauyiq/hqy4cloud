package com.hqy.cloud.actuator.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * actuator是spring boot提供的对应用系统的自省和监控的集成功能，可以对应用系统进行配置查看、相关功能统计等<br>
 * 为非Springboot目提供同样的支持
 * @author qiyuan.hong
 * @date 2024/10/17
 */
public interface ActuatorService extends Remote {

    /**
     * 公共的Actuator节点父路径
     */
    String BASE_PATH_ACTUATOR = "/actuator";

    /**
     * 打印线程栈+线程、守护线程的数量，(不同于threadsDumpDetails，这个方法是简化版的)
     * <br> 内部已优化，类似的线程栈会自动分组（如果线程数量特别多的场合）
     * @return
     * @throws RemoteException
     */
    String threadsDump() throws RemoteException;

    /**
     * 打印线程栈+线程、守护线程的数量，强制输出所有的线程的明细的栈信息，方便调试；<br>
     * 可能数据量较大，请谨慎调用...
     * @return
     * @throws RemoteException
     */
    List<String> threadsDumpDetails() throws RemoteException;

    /**
     * 查看所有环境变量
     * @return
     * @throws RemoteException
     */
    String env() throws RemoteException;

    /**
     * 查看应用健康指标（网络断开重连相关事件存储在这里）
     * @return
     * @throws RemoteException
     */
    String health() throws RemoteException;









}
