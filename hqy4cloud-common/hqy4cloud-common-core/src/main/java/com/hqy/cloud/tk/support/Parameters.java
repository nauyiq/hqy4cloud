package com.hqy.cloud.tk.support;

import com.hqy.cloud.common.base.lang.StringConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/7/8 16:54
 */
public abstract class Parameters {

    protected transient Map<String, String> parameters = new HashMap<>();

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
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

    public String getParameter(String key, String defaultValue) {
        if (!isValidParams()) {
            return defaultValue;
        }
        return parameters.getOrDefault(key, defaultValue);
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
