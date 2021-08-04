package com.hqy.common.swticher;

/**
 *  HTTP节点通用开关；跟业务无关，具有共性的开关定义在这里，取值范围101~150 <br>
 *  例如，是否开启http采集？是否开启web防sql注入Filter？<br>
 *  注意：开关id在100以上的是通用开关。
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-30 11:28
 */
public class HttpGeneralSwitcher extends CommonSwitcher {


    /**
     * 节点(Web)-是否启用http请求体 可重复读的请求包装过滤器 20210630 默认开
     */
    public static final HttpGeneralSwitcher ENABLE_REPEAT_READABLE_HTTP_REQUEST_WRAPPER_FILTER = new HttpGeneralSwitcher(101,"节点(Web)-是否启用http请求体 可重复读的请求包装过滤器",true);


    /**
     * 场景： 启用配置化的Ip访问限制策略（126） 节点后 ，  黑客行为检测 规则:   如果false使用原来的检测(一次黑客行为就拉黑);    true则走限制 配置化的规则(次数可以配置)
     */
    public static final HttpGeneralSwitcher ENABLE_IP_RATE_LIMIT_HACK_CHECK_RULE = new HttpGeneralSwitcher(128,"节点-在126开关的Ip限流策略下是否容忍多次(次数可以配置)黑客探测行为", false);


    /**
     * 节点-是否启用HTTP限流器，限制请求频度（1分钟一个ip 限制若干次请求）
     */
    public static final HttpGeneralSwitcher ENABLE_HTTP_THROTTLE_VALVE = new HttpGeneralSwitcher(150,"节点-是否启用通用HTTP限流器",true);

    /**
     * 节点-是否启用(URI 和请求参数中xss攻击防范)参数校验的限流器...
     */
    public static final HttpGeneralSwitcher ENABLE_HTTP_THROTTLE_SECURITY_CHECKING = new HttpGeneralSwitcher(151,"节点-是否启用HTTP限流安全侦测(黑客)",true);


    /**
     * 节点-是否所有项目共享超限统计计数方式
     */
    public static final HttpGeneralSwitcher ENABLE_SHARE_IP_OVER_REQUEST_STATISTICS = new HttpGeneralSwitcher(152, "节点-是否所有项目共享超限统计计数方式", true);



    protected HttpGeneralSwitcher(int id, String name, boolean status) {
        super(id, name, status);
    }
}
