package com.hqy.util.config;

import com.hqy.base.common.base.lang.BaseStringConstants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * yml配置文件加载策略
 * @author qiyuan.hong
 * @version 1.0
 * @date 2022/3/4 10:20
 */
@SuppressWarnings("unchecked")
public class YamlStrategy extends AbstractConfigStrategy {

    private static final Logger log = LoggerFactory.getLogger(YamlStrategy.class);

    public YamlStrategy(String propertyName) {
        super(propertyName);
    }

    /**
     * 存放当前yaml文件的数据
     */
    private Map<String, String> data;


    @Override
    protected void loadConfig(InputStream inputStream) {
        try {
            Yaml yaml = new Yaml();
            Map<String, Object> loadAs = yaml.loadAs(inputStream, Map.class);
            data = getYmlData(loadAs);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> getYmlData(Map<String, Object> ymlData) {
        data = new HashMap<>(64);
        for (Map.Entry<String, Object> entry : ymlData.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            if (val instanceof Map) {
                foreachYaml(key, (Map<String, Object>) val, 1);
            } else {
                data.put(key, val.toString());
            }
        }
        return data;
    }

    /**
     * 通过递归 遍历yml文件, 获取map集合
     * @param keyStr
     * @param objectMap
     * @param step
     */
    public void foreachYaml(String keyStr, Map<String, Object> objectMap, int step) {
        for (Map.Entry<String, Object> entry : objectMap.entrySet()) {
            String key = entry.getKey();
            Object val = entry.getValue();
            String newStr;
            if (StringUtils.isNotEmpty(keyStr)) {
                newStr = keyStr + BaseStringConstants.Symbol.POINT + key;
            } else {
                newStr = key;
            }
            if (val instanceof Map) {
                foreachYaml(newStr, (Map<String, Object>) val, ++step);
            } else {
                data.put(newStr, val.toString());
            }
        }
    }


    public Map<String, String> getData() {
        return data;
    }
}
