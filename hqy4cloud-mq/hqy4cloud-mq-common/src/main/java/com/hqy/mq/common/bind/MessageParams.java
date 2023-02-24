package com.hqy.mq.common.bind;

import cn.hutool.core.convert.Convert;
import com.hqy.cloud.tk.support.Parameters;
import org.apache.commons.lang3.StringUtils;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2023/2/8 9:12
 */
public class MessageParams extends Parameters {

    /**
     * 主题/交换机
     */
    private String target;

    /**
     * 路由键/key
     */
    private String key;

    public MessageParams() {
    }

    public MessageParams(String target, String key) {
        this.target = target;
        this.key = key;
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Integer getInt(String key, Integer defaultValue) {
        String parameter = getParameter(key);
        return StringUtils.isNotBlank(parameter) ? Convert.toInt(parameter) : defaultValue;
    }

    public Long getLong(String key) {
        return getLong(key, null);
    }

    public Long getLong(String key, Long defaultValue) {
        String parameter = getParameter(key);
        return StringUtils.isNotBlank(parameter) ? Convert.toLong(parameter) : defaultValue;
    }

    public Boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        String parameter = getParameter(key);
        return StringUtils.isNotBlank(parameter) ? Convert.toBool(parameter) : defaultValue;
    }


    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
