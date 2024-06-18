package com.hqy.cloud.web.filter;


import com.hqy.cloud.util.XssUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/5/15 11:36
 */
public class XssWrapper extends HttpServletRequestWrapper {

    /**
     * Constructs a request object wrapping the given request.
     * @param request The request to wrap
     * @throws IllegalArgumentException if the request is null
     */
    public XssWrapper(HttpServletRequest request) {
        super(request);
    }

    /**
     * 对数组参数进行特殊字符过滤
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXss(values[i]);
        }
        return encodedValues;
    }

    /**
     * 对参数中特殊字符进行过滤
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (StringUtils.isBlank(value)) {
            return value;
        }
        return cleanXss(value);
    }

    /**
     * 获取attribute,特殊字符过滤
     */
    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String && StringUtils.isNotBlank((String) value)) {
            return cleanXss((String) value);
        }
        return value;
    }

    /**
     * 对请求头部进行特殊字符过滤
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (StringUtils.isBlank(value)) {
            return value;
        }
        return cleanXss(value);
    }

    private String cleanXss(String value) {
        return XssUtil.clean(value);
    }


}
