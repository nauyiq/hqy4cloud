package com.hqy.gateway.flow;

import com.hqy.common.swticher.CommonSwitcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 基于Redis的流量控制器;流量控制管理中心；
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-08-04 14:14
 */
@Slf4j
public enum RedisFlowControlCenter {

    INSTANCE;

    private static RedisFlowController getController = null;
    private static RedisFlowController postController = null;

    private static final FlowControlConfig DEFAULTFlowConfig = new FlowControlConfig();

    public FlowControlConfig getProjectFlowControlConfig() {
        //TODO 暂时所有项目返回默认配置 后续可以改造成根据项目返回配置
        return DEFAULTFlowConfig;
    }


    /**
     * 笼统的针对每个ip请求总数的超限判断.<br>
     * 如需判定 针对单个uri的超限判定，请使用 needLimitPerTimeWindow4Uri 方法；
     *
     * @param remoteAddr
     * @param method
     * @param uri
     * @return
     */
    public RedisFlowDTO needLimitPerTimeWindow(String remoteAddr, String method, String uri) {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("needLimitPerMinute | {} |  {}", remoteAddr, method);
        }
        if (HttpMethod.OPTIONS.matches(method) || HttpMethod.TRACE.matches(method) || HttpMethod.PATCH.matches(method)) {
            //非业务方法不限制
            return new RedisFlowDTO(null, false, 0L, -1, 0);
        }
        //笼统的针对每个ip的方法请求总数超限判断
        FlowControlConfig config = getProjectFlowControlConfig();
        //根据请求方法和相应的 超限配置策略 获取到流量控制器
        RedisFlowController redisFlowController = getRedisFlowController(config, HttpMethod.resolve(method));
        if (Objects.isNull(redisFlowController)) {
            //获取流量控制器失败...
            log.warn("### NO RedisFlowController config for request {}, remoteAddr={} ", uri, remoteAddr);
            return new RedisFlowDTO(null, false, 0L, -1);
        }
        //判断是否超限
        String redisInnerKey = remoteAddr.concat(":").concat(formatMinutes(config.getWindow()));
        RedisFlowDTO flowDTO = redisFlowController.isOverLimit(redisInnerKey);
        if(CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("[{}] Ip Rate Is Over Limit ? {}",remoteAddr, flowDTO.toString());
        }
        if (flowDTO.getOverLimit()) {
            flowDTO.setBlockSeconds(config.getBlockSeconds());
        }
        return flowDTO;
    }


    /**
     * 根据请求方法和相应的 超限配置策略 获取到流量控制器
     *
     * @param config
     * @param method
     * @return
     */
    private RedisFlowController getRedisFlowController(FlowControlConfig config, HttpMethod method) {
        if (HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method)) {
            if (Objects.isNull(getController)) {
                getController = new RedisFlowController(config.getMaxGet(), config.getExpireSeconds());
            }
            return getController;
        }

        if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.DELETE.equals(method)) {
            if (Objects.isNull(postController)) {
                postController = new RedisFlowController(config.getMaxPost(), config.getExpireSeconds());
            }
            return postController;
        }
        return null;
    }

    /**
     * 获取当前时间的 分钟级别的字符串显示形式
     * @param mm
     * @return
     */
    public static String formatMinutes(MeasurementMinutes mm) {
        Date date = new Date();
        SimpleDateFormat sdfDay = null;
        String tail = "";
        if (mm == MeasurementMinutes.ONE_MINUTE) {
            sdfDay = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
        } else if (mm == MeasurementMinutes.FIVE_MINUTES) {
            sdfDay = new SimpleDateFormat("yyyy-MM-dd:HH");
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int m5 = minute / 10;
            int mod5 = minute % 10;
            if (m5 == 0) {
                tail = "-0" + (mod5 < 5 ? 0 : 5);
            } else {
                tail = "-" + ((m5 * 10) + (mod5 < 5 ? 0 : 5));
            }
        } else if (mm == MeasurementMinutes.TEN_MINUTES) {
            sdfDay = new SimpleDateFormat("yyyy-MM-dd:HH");
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int m5 = minute / 10;
            if (m5 == 0) {
                tail = ":00";
            } else {
                tail = ":" + (m5 * 10);
            }
        } else if (mm == MeasurementMinutes.THIRTY_MINUTES) {
            //半小时，30分钟
            sdfDay = new SimpleDateFormat("yyyy-MM-dd:HH");
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int m5 = minute / 30;
            if (m5 == 0) {
                tail = ":00";
            } else {
                tail = ":" + (m5 * 30);
            }
        } else if (mm == MeasurementMinutes.ONE_HOUR_MINUTES) {
            //1 小时
            sdfDay = new SimpleDateFormat("yyyy-MM-dd:HH");
        } else {
            //一天，24小时
            sdfDay = new SimpleDateFormat("yyyy-MM-dd");
        }
        return sdfDay.format(date).concat(tail);
    }

}
