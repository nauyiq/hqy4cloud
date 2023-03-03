package com.hqy.timer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import com.hqy.timer.service.TimerFoundationService;
import com.hqy.cloud.util.CommonDateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.quartz.impl.DirectSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

/**
 * 基于Quartz Scheduler 的GFW周期性定时任务....<br>
 * 需要手动startUp ，去掉了spring自动启动；<br>
 * 因为不是每个项目都需要调度器，都配置了xkScheduler<br>
 * @author qy
 * @date 2021-09-15 15:28
 */
@Service
public class QuartzTimerServiceImpl implements TimerFoundationService {

    private static final Logger log = LoggerFactory.getLogger(QuartzTimerServiceImpl.class);

    /**
     * 最小6个或者cpu核数  两者之间的大值
     */
    public static int coreSize = 6;

    @Autowired(required = false)
    private Scheduler scheduler = null;

    /**
     * 是否已启动？ 防止多次...
     */
    private static final boolean STARTED = false;

    /**
     * 手动启动quartz调度器
     */
    @Override
    public void startUp() {
        String host =  NetUtil.getLocalhostStr();
        log.info("### QuartzTimerService ready to startUp -> {}", host);
        if (STARTED) {
            log.warn("### Start QuartzTimerService error, timerService already start up.");
            return;
        }
        try {
            if (scheduler == null) {
                int cpu = Runtime.getRuntime().availableProcessors();
                if (cpu > coreSize) {
                    coreSize = cpu;
                }
                log.info("### createVolatileScheduler :{}", coreSize);
                // 创建一个拥有coreSize个线程的调度程序
                DirectSchedulerFactory.getInstance().createVolatileScheduler(coreSize);
                // 启动调度程序
                DirectSchedulerFactory.getInstance().getScheduler().start();
                //quartz scheduler
                scheduler = DirectSchedulerFactory.getInstance().getScheduler();
            } else {
                log.info("### use @Autowired Scheduler");
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 检查定时任务是否存在
     * @param id
     * @param group
     * @return
     */
    @Override
    public boolean checkExistJob(String id, String group) {
        JobKey jobKey = new JobKey(id, group);
        try {
            return scheduler.checkExists(jobKey);
        } catch (Exception e) {
            log.error("### checkExistJob error, id:{}, group:{}", id, group);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 打断某个定时任务
     * @param id
     * @param group
     * @return
     */
    @Override
    public boolean interruptJob(String id, String group) {
        JobKey jobKey = new JobKey(id, group);
        try {
            return scheduler.interrupt(jobKey);
        } catch (Exception e) {
            log.error("### interruptJob error, id:{}, group:{}", id, group);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 移除某个job； 强烈建议 移除前，先 checkExistJob 检查要移除的job是否存在...
     * @param id
     * @param group
     * @return
     */
    @Override
    public boolean removeJob(String id, String group) {
        JobKey jobKey = new JobKey(id, group);
        try {
            return scheduler.deleteJob(jobKey);
        } catch (Exception e) {
            log.error("### removeJob error, id:{}, group:{}", id, group);
            log.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * 添加一个每隔一天运行一次的job;
     * @param time  HH:mm
     * @param id
     * @param group
     * @param job
     * @return
     */
    @Override
    public void addDailyJob(String time, String id, String group, Job job) {
        addDaysJob(time, id, group, 1, job);
    }

    /**
     * 添加一个每隔days天运行一次的job;
     * @param time 格式 HH:mm
     * @param id
     * @param group
     * @param days
     * @param job
     */
    @Override
    public void addDaysJob(String time, String id, String group, Integer days, Job job) {
        JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(id, group).build();
        Date runTime = CommonDateUtil.getHourAndMinute(null, time);
        if (runTime == null) {
            throw new RuntimeException("格式化时间异常, time:" + time);
        }
        if (runTime.before(new Date())) {
            runTime = DateUtil.offsetDay(runTime, 1);
        }

        SimpleScheduleBuilder sBuilder = SimpleScheduleBuilder.simpleSchedule().repeatForever()
                .withIntervalInHours(24 * days).withMisfireHandlingInstructionFireNow();

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id, group).startAt(runTime)
                .withSchedule(sBuilder).build();

        try {
             scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 添加一个每隔1月运行一次的job
     * 每月1号，1点45分运行
     * @param id
     * @param group
     * @param job
     */
    @Override
    public void addMonthJob(String id, String group, Job job) {
        JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(id, group).build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id, group)
                .withSchedule(
                        CronScheduleBuilder.cronSchedule("0 45 1 1 * ?").withMisfireHandlingInstructionFireAndProceed()
                ).forJob(jobDetail).build();
        try {
            scheduler.scheduleJob(trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 添加一个循环的按cron 表达式运行的job
     * @param id
     * @param group
     * @param cron
     * @param job
     */
    @Override
    public void addCronJob(String id, String group, String cron, Job job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(id, group).build();
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id, group)
                    .withSchedule(
                            CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionFireAndProceed()
                    ).forJob(jobDetail).build();
            scheduler.scheduleJob(trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 添加每隔 hours 小时运行一次的job
     * @param id
     * @param group
     * @param hours
     * @param job
     */
    @Override
    public void addHoursJob(String id, String group, int hours, Job job) {
        addHoursJob(id, group, hours, null, job);
    }

    /**
     * 添加每隔 hours 小时运行一次的job
     * @param id
     * @param group
     * @param hours
     * @param minutes
     * @param job
     */
    @Override
    public void addHoursJob(String id, String group, int hours, Integer minutes, Job job) {
        try {
            Date runTime = DateUtils.truncate(new Date(), Calendar.HOUR);
            if (minutes != null && minutes != 0) {
                runTime = DateUtils.addMinutes(runTime, minutes);
                if (runTime.before(new Date())) {
                    runTime = DateUtils.addHours(runTime, 1);
                }
            }
            JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(id, group).build();

            SimpleScheduleBuilder sBuilder = SimpleScheduleBuilder.simpleSchedule().repeatForever()
                    .withIntervalInHours(hours).withMisfireHandlingInstructionIgnoreMisfires();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id, group).startAt(runTime)
                    .withSchedule(sBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 添加一个每隔minutes分钟定期运行一次的job
     * @param id
     * @param group
     * @param job
     */
    @Override
    public void addMinutesJob(String id, String group, int minutes, Job job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(id, group).build();
            Date runTime = DateUtils.truncate(new Date(), Calendar.MINUTE);

            SimpleScheduleBuilder sBuilder = SimpleScheduleBuilder.simpleSchedule().repeatForever()
                    .withIntervalInMinutes(minutes).withMisfireHandlingInstructionIgnoreMisfires();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id, group).startAt(runTime)
                    .withSchedule(sBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 添加 一个seconds若干秒仅仅运行一次的job
     * @param id
     * @param group
     * @param seconds
     * @param job
     */
    @Override
    public void addSecondsJobOnce(String id, String group, int seconds, Job job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(id, group).build();
            Date runTime = DateUtils.addSeconds(new Date(), seconds);
            SimpleScheduleBuilder sBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInSeconds(seconds).withMisfireHandlingInstructionFireNow();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(id, group)
                    .startAt(runTime)
                    .withSchedule(sBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 添加一个每隔seconds秒定期运行一次的job
     * @param id
     * @param group
     * @param seconds
     * @param job
     */
    @Override
    public void addSecondsJob(String id, String group, int seconds, Job job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(id, group).build();
            Date runTime = DateUtils.addSeconds(new Date(), seconds);
            SimpleScheduleBuilder sBuilder = SimpleScheduleBuilder.simpleSchedule().repeatForever()
                    .withIntervalInSeconds(seconds).withMisfireHandlingInstructionIgnoreMisfires();

            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id, group).startAt(runTime)
                    .withSchedule(sBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 添加一个minutes分钟后仅仅运行一次的job
     * @param id
     * @param group
     * @param minutes
     * @param job
     */
    @Override
    public void addMinutesJobOnce(String id, String group, int minutes, Job job) {
        try {
            JobDetail jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(id, group).build();

            Date runTime = DateUtils.addSeconds(new Date(), minutes);

            SimpleScheduleBuilder sBuilder = SimpleScheduleBuilder.simpleSchedule()
                    .withIntervalInMinutes(minutes).withMisfireHandlingInstructionFireNow();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(id, group)
                    .startAt(runTime)
                    .withSchedule(sBuilder).build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
