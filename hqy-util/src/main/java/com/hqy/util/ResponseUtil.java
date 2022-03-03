package com.hqy.util;

import com.hqy.fundation.common.base.lang.BaseStringConstants;
import com.hqy.fundation.common.bind.DataResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/23 16:19
 */
@Slf4j
public class ResponseUtil {


    public static void out(HttpServletResponse response,  DataResponse dataResponse) {
        out(response, BaseStringConstants.APPLICATION_JSON_UTF_8, 200, dataResponse, null);
    }

    public static void out(HttpServletResponse response, int statusCode, DataResponse dataResponse) {
        out(response, BaseStringConstants.APPLICATION_JSON_UTF_8, statusCode, dataResponse, null);
    }


    /**
     * response输出
     * @param response HttpServletResponse
     * @param contentType contentType
     * @param statusCode http状态码
     * @param dataResponse 相应数据
     */
    public static void out(HttpServletResponse response, String contentType, int statusCode, DataResponse dataResponse, Map<String, String> headers) {
        try {
            if (Objects.isNull(response)) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (Objects.nonNull(servletRequestAttributes)) {
                    response = servletRequestAttributes.getResponse();
                }
            }
            if (Objects.nonNull(response) && !response.isCommitted()) {
                //是否乱码
                response.setCharacterEncoding("UTF-8");
                response.setContentType(contentType);
                response.setStatus(statusCode);
                if (!CollectionUtils.isEmpty(headers)) {
                    for (String header : headers.keySet()) {
                        response.setHeader(header, headers.get(header));
                    }
                }
                response.getWriter().write(JsonUtil.toJson(dataResponse));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


}
