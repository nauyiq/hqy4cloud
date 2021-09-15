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
    boolean removeJob(String id, String group);

    /**
     *  添加一个每隔一天运行一次的job;
     * @param time  HH:mm
     * @param id
     * @param group
     * @param job
     * @return
     */
    void addDailyJob(String time, String id, String group, Job job);

    /**
     * 添加一个每隔days天运行一次的job;
     * @param time
     * @param id
     * @param group
     * @param days
     * @param job
     */
    void addDaysJob(String time, String id, String group, Integer days, Job job);


    /**
     * 添加一个每隔1月运行一次的job
     * @param id
     * @param group
     * @param job
     */
    void addMonthJob(String id, String group, Job job);

    /**
     * 添加一个循环的按cron 表达式运行的job
     * @param id
     * @param group
     * @param cron
     * @param job
     */
    void addCronJob(String id, String group, String cron, Job job);


    /**
     * 添加每隔 hours 小时运行一次的job
     * @param id
     * @param group
     * @param hours
     * @param job
     */
    void addHoursJob(String id, String group, int hours, Job job);

    /**
     * 添加每 隔hours 小时运行一次的job
     * @param id
     * @param group
     * @param hours
     * @param minutes
     * @param job
     */
    void addHoursJob(String id, String group, int hours, Integer minutes, Job job);

    /**
     * 添加一个每隔minutes分钟定期运行一次的job
     * @param id
     * @param group
     * @param job
     */
    void addMinutesJob(String id, String group, int minutes, Job job);

    /**
     * 添加一个每隔seconds秒定期运行一次的job
     * @param id
     * @param group
     * @param seconds
     * @param job
     */
    void addSecondsJob(String id, String group, int seconds, Job job);


    /**
     * 添加 一个seconds若干秒仅仅运行一次的job
     * @param id
     * @param group
     * @param seconds
     * @param job
     */
    void addSecondsJobOnce(String id, String group, int seconds, Job job) ;

    /**
     * 添加一个minutes分钟后仅仅运行一次的job
     * @param id
     * @param group
     * @param minutes
     * @param job
     */
    void addMinutesJobOnce(String id, String group, int minutes, Job job);


}
