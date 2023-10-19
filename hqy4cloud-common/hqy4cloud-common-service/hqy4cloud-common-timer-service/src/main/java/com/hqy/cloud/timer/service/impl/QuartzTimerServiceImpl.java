package com.hqy.cloud.timer.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.net.NetUtil;
import com.hqy.cloud.timer.service.TimerFoundationService;
import com.hqy.cloud.util.CommonDateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.quartz.impl.DirectSchedulerFactory;
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
@Slf4j
@Service
public class QuartzTimerServiceImpl implements TimerFoundationService {

    private Scheduler scheduler = null;

    /**
     * 最小6个或者cpu核数  两者之间的大值
     */
    public static int coreSize = 6;

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


    @Override
    public void addDailyJob(String time, String id, String group, Job job) {
        addDaysJob(time, id, group, 1, job);
    }


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


    @Override
    public void addHoursJob(String id, String group, int hours, Job job) {
        addHoursJob(id, group, hours, null, job);
    }


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
