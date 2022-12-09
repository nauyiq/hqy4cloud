package com.hqy.access.flow.server;

import com.hqy.access.flow.FlowResult;
import com.hqy.access.flow.Measurement;
import com.hqy.access.flow.strategy.AccessFlowController;
import com.hqy.base.common.swticher.CommonSwitcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

/**
 * 基于Redis的流量控制器;流量控制管理中心；
 * @author qy
 * @date 2021-08-04 14:14
 */
@Slf4j
public class HttpAccessFlowControlCenter {

    private final AccessFlowController getController;

    private final AccessFlowController postController;

    private final AccessFlowController uriController;

    public HttpAccessFlowControlCenter(FlowConfigProperties properties) {
        this.getController = new AccessFlowController(properties.getGetLimitConfig());
        this.postController = new AccessFlowController(properties.getPostLimitConfig());
        this.uriController = new AccessFlowController(properties.getUriLimitConfig());
    }

    /**
     * 笼统的针对每个ip请求总数的超限判断.<br>
     * 如需判定 针对单个uri的超限判定，请使用 needLimitPerTimeWindow4Uri 方法；
     * @param remoteIp 待判断ip
     * @param method 方法
     * @param uri 请求uri
     * @return redis流量限流器的检测结果
     */
    public FlowResult needLimitPerTimeWindow(String remoteIp, String method, String uri) {
        if (CommonSwitcher.JUST_4_TEST_DEBUG.isOn()) {
            log.info("needLimitPerMinute | {} |  {}", remoteIp, method);
        }

        if (HttpMethod.OPTIONS.matches(method) || HttpMethod.TRACE.matches(method) || HttpMethod.PATCH.matches(method)) {
            //非业务方法不限制
            return FlowResult.build();
        }
        //根据请求方法和相应的 超限配置策略 获取到流量控制器
        AccessFlowController accessFlowController = getAccessFlowController(HttpMethod.resolve(method));
        if (Objects.isNull(accessFlowController)) {
            //获取流量控制器失败...
            log.warn("### NO RedisFlowController config for request {}, remoteIp={} ", uri, remoteIp);
            return FlowResult.build();
        }
        FlowResult result = accessFlowController.isOverLimit(remoteIp);
        if (result.isOverLimit()) {
            result.setBlockSeconds(accessFlowController.getLimiter().getConfig().getBlockSeconds());
        }
        return result;
    }


    /**
     * 根据请求方法和相应的 超限配置策略 获取到流量控制器
     * @param method http method
     * @return 流量控制器
     */
    private AccessFlowController getAccessFlowController(HttpMethod method) {
        if (HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method)) {
            return getController;
        }
        if (HttpMethod.POST.equals(method) || HttpMethod.PUT.equals(method) || HttpMethod.DELETE.equals(method)) {
            return postController;
        }
        return null;
    }

    /**
     * 获取当前时间的 分钟级别的字符串显示形式
     * @param mm 分钟
     * @return 分钟级别的字符串显示形式
     */
    public static String formatMinutes(Measurement.MeasurementMinutes mm) {
        Date date = new Date();
        SimpleDateFormat sdfDay = new SimpleDateFormat("yyyy-MM-dd:HH");;
        String tail = "";

        if (mm == Measurement.MeasurementMinutes.ONE_MINUTE) {
            //1分钟
            sdfDay = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
        } else if (mm == Measurement.MeasurementMinutes.FIVE_MINUTES) {
            //5分钟
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int m5 = minute / 10;
            int mod5 = minute % 10;
            if (m5 == 0) {
                tail = "-0" + (mod5 < 5 ? 0 : 5);
            } else {
                tail = "-" + ((m5 * 10) + (mod5 < 5 ? 0 : 5));
            }
        } else if (mm == Measurement.MeasurementMinutes.TEN_MINUTES) {
            //10分钟
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int m5 = minute / 10;
            if (m5 == 0) {
                tail = ":00";
            } else {
                tail = ":" + (m5 * 10);
            }
        } else if (mm == Measurement.MeasurementMinutes.THIRTY_MINUTES) {
            //半小时，30分钟
            int minute = Calendar.getInstance().get(Calendar.MINUTE);
            int m5 = minute / 30;
            if (m5 == 0) {
                tail = ":00";
            } else {
                tail = ":" + (m5 * 30);
            }
        } else {
            //一天，24小时
            sdfDay = new SimpleDateFormat("yyyy-MM-dd");
        }

        return sdfDay.format(date).concat(tail);
    }

}
