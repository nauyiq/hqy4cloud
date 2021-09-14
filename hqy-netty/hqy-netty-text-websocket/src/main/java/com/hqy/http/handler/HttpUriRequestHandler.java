package com.hqy.http.handler;

/**
 *  业务系统处理http接口的处理器，
 *  1 不建议应用层try-catch异常，建议抛出，由框架层统一出来；除非业务层有必要主动自行维护异常；
 *  2 建议请求参数从HttpRequestDTO requestDTO这个包装后的实体 中获取，不要自行再次解析原始的netty的fullHttpRequest
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-22 17:23
 */
public interface HttpUriRequestHandler {
}
