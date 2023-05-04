package com.hqy.cloud.util;

import cn.hutool.http.ContentType;
import com.hqy.cloud.common.base.lang.StringConstants;
import com.hqy.cloud.common.bind.DataResponse;
import com.hqy.cloud.common.bind.R;
import com.hqy.cloud.common.result.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/2/23 16:19
 */
@Slf4j
public class ResponseUtil {

    /**
     * response输出
     * @param response HttpServletResponse
     * @param statusCode http状态码
     */
    public static <T> void out(HttpServletResponse response, int statusCode, R<T> result) {
        try {
            if (Objects.isNull(response)) {
                ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (Objects.nonNull(servletRequestAttributes)) {
                    response = servletRequestAttributes.getResponse();
                }
            }

            if (Objects.nonNull(response) && !response.isCommitted()) {
                //是否乱码
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setContentType(ContentType.JSON.getValue());
                response.setStatus(statusCode);
                response.getWriter().write(JsonUtil.toJson(result));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


}
