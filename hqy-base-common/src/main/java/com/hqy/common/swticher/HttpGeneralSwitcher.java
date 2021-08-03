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
     * 节点-是否启用(URI 和请求参数中xss攻击防范)参数校验的限流器...
     */
    public static final HttpGeneralSwitcher ENABLE_HTTP_THROTTLE_SECURITY_CHECKING = new HttpGeneralSwitcher(151,"节点-是否启用HTTP限流安全侦测(黑客)",true);


    protected HttpGeneralSwitcher(int id, String name, boolean status) {
        super(id, name, status);
    }
}
