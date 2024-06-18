package com.hqy.cloud.common.base;

import com.hqy.cloud.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/8 16:54
 */
public abstract class Parameters {

    protected transient Map<String, String> parameters = new ConcurrentHashMap<>();

    /**
     * cache.
     */
    private volatile transient Map<String, Number> numbers;

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public void addParameters(Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public boolean isValidParams() {
        return !this.parameters.isEmpty();
    }

    public String getParameter(String key) {
        return getParameter(key, StringConstants.EMPTY);
    }


    public long getParameter(String key, long defaultValue) {
        Number number = getNumbers().get(key);
        if (number != null) {
            return number.longValue();
        }
        String value  = getParameter(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        long longValue = Long.parseLong(value);
        getNumbers().put(key, longValue);
        return longValue;
    }

    public int getParameter(String key, int defaultValue) {
        Number number = getNumbers().get(key);
        if (number != null) {
            return number.intValue();
        }
        String value  = getParameter(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        int intValue = Integer.parseInt(value);
        getNumbers().put(key, intValue);
        return intValue;
    }

    public Map<String, Number> getNumbers() {
        if (numbers == null) {
            numbers = new ConcurrentHashMap<>(16);
        }
        return numbers;
    }

    public String getParameter(String key, String defaultValue) {
        if (!isValidParams()) {
            return defaultValue;
        }
        return parameters.getOrDefault(key, defaultValue);
    }

    public boolean getParameter(String key, boolean defaultValue) {
        String value  = getParameter(key);
        if (StringUtils.isBlank(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public void setParameter(String key, String value) {
        if (StringUtils.isNotBlank(key)) {
            parameters.put(key, value);
        }
    }


    /**
     * Get the parameters to be selected(filtered)
     * @param nameToSelect the {@link Predicate} to select the parameter name
     * @return non-null {@link Map}
     */
    public Map<String, String> getParameters(Predicate<String> nameToSelect) {
        Map<String, String> selectedParameters = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : getParameters().entrySet()) {
            String name = entry.getKey();
            if (nameToSelect.test(name)) {
                selectedParameters.put(name, entry.getValue());
            }
        }
        return Collections.unmodifiableMap(selectedParameters);
    }








}
