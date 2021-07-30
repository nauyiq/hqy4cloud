package com.hqy.global;

import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.Map;

/**
 * 请求体可重复读的HttpServletRequest<br>
 * @author qy
 * @project: hqy-parent-all
 * @create 2021-07-28 18:20
 */
public class RepeatableReadHttpRequest extends HttpServletRequestWrapper {

//    private final byte[] body;

    private String bodyString;

    private String queryString;

    private String requestIp;

    /**** https://www.cnblogs.com/Sinte-Beuve/p/13260249.html ****/
    private static final String FORM_CONTENT_TYPE_MULTIPART = "multipart/form-data";

    /**
     * 是否是multi-part 的请求， 如果是 请 use original request
     */
    private boolean isMultiPartRequest = false;

    private ContentCachingRequestWrapper proxy;

    private Map<String, String> parameterMapEx;

    public String getRequestIp() {
        return requestIp;
    }

    /**
     * 当请求类型是contentType.equals("application/json") 时候，特殊处理，请求json串装入到params 中的key值
     */
    public static final String APPLICATION_JSON = "application/json";

    /**
     * Constructs a request object wrapping the given request.
     * @param request
     * @throws IllegalArgumentException if the request is null
     */
    public RepeatableReadHttpRequest(HttpServletRequest request) {
        super(request);
        proxy = new ContentCachingRequestWrapper(request);
        queryString = request.getQueryString();





    }
}
