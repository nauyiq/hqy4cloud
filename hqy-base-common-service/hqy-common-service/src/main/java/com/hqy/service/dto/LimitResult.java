package com.hqy.service.dto;

/**
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-27 20:12
 */
public class LimitResult {

    /**
     * 是否限行， true表示拦截， false放行
     */
    private Boolean limit = false;

    /**
     * 拦截的辅助提示信息，url，ua等等...
     */
    private String tip;

    /**
     * 拦截的原因
     */
    private ReasonEnum reason = ReasonEnum.NOT_ENABLE_HTTP_THROTTLE_OK;

    public LimitResult(Boolean limit, ReasonEnum reason) {
        this.limit = limit;
        this.reason = reason;
    }

    public LimitResult(Boolean limit, String tip, ReasonEnum reason) {
        this.limit = limit;
        this.tip = tip;
        this.reason = reason;
    }

    public LimitResult() {
    }



    public enum ReasonEnum {

        /**
         * 无条件放行，
         */
        NONE_OK(100),

        /**
         * 未启用节流器，放行，
         */
        NOT_ENABLE_HTTP_THROTTLE_OK(101),

        /**
         * 服务器刚刚才启动，免检 放行，
         */
        SERVER_JUST_STARTUP_OK(102),

        /**
         * ip白名单，放行
         */
        WHITE_IP_OK(106),

        /**
         * URI 白名单 OK
         */
        WHITE_URI_OK(108),

        /**
         * 静态资源请求访问，放行
         */
        STATIC_REQUEST_OK(109),

        /**
         * DB忙，限行
         */
        DB_BUSY_NG(50401),

        /**
         * 线程池忙，限行
         */
        THREAD_POOL_BUSY_NG(50402),

        /**
         * 参数异常，限行
         */
        PARAM_ERROR_NG(50404),

        /**
         * 访问速度超速，  ，限行
         */
        RATE_LIMIT_NG(50406),

        /**
         * 人工指定的黑名单，，限行
         */
        MANUAL_BLOCKED_IP_NG(50408),

        /**
         * BI行为分析的黑名单ip，限行
         */
        BI_BLOCKED_IP_NG(50500),

        /**
         * hack工具访问，限行
         */
        HACK_TOOL_ACCESS_NG(50501),

        /**
         * 多次尝试访问不存在的接口，限行
         */
        ACCESS_NON_INTERFACE_NG(50504);


        ;

        public int code;

        ReasonEnum(int code) {
            this.code = code;
        }
    }


    public Boolean getLimit() {
        return limit;
    }

    public void setLimit(Boolean limit) {
        this.limit = limit;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public ReasonEnum getReason() {
        return reason;
    }

    public void setReason(ReasonEnum reason) {
        this.reason = reason;
    }

    public boolean isNeedLimit() {
        return limit;
    }
}
