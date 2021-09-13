package com.hqy.fundation.timer;

import org.quartz.Job;

/**
 * 全局的周期性定时任务 service
 * 基于Quartz Scheduler中 在一个Scheduler中这二者的组合必须是唯一的。
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-09-13 19:16
 */
public interface TimerFoundationService {

    /**
     * 启动调度器，启动任务
     */
    void startUp();

    /**
     * 检查定时任务是否存在
     * @param id
     * @param group
     * @return
     */
    boolean checkExistJob(String id, String group);

    /**
     * 打断某个定时任务
     * @param id
     * @param group
     * @return
     */
    boolean interruptJob(String id, String group);

    /**
     * 移除某个job； 强烈建议 移除前，先 checkExistJob 检查要移除的job是否存在...
     * @param id
     * @param group
     * @return
     */
    boolean removeJon(String id, String group);

    /**
     *  添加一个每隔一天运行一次的job;
     * @param time  HH:mm
     * @param id
     * @param group
     * @param job
     * @return
     */
    boolean addDailyJob(String time, String id, String group, Job job);
}
