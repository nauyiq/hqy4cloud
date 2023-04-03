package com.hqy.cloud.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JSON实用类
 *
 * @author hongqy
 * @date 2021-07-08 17:10
 */
@Slf4j
public class JsonUtil {

    private final static ObjectMapper MAPPER = new ObjectMapper();
    private static final String EMPTY_STR_OBJ = "{}";

    static {
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        MAPPER.setDateFormat(dateFormat);
    }

    public static void registerModule(Module module) {
        MAPPER.registerModule(module);
    }

    /**
     * Bean对象转Json字符串
     * @param bean obj.
     * @return 	   json.
     */
    public static String toJson(Object bean) {
        try {
            if (bean == null) {
                return EMPTY_STR_OBJ;
            }
            return MAPPER.writeValueAsString(bean);
        } catch (Exception e) {
            log.error("toJson is error.bean:{}", bean);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static byte[] toJsonBytes(Object bean) {
        try {
            if (Objects.isNull(bean)) {
                return new byte[0];
            }
            return MAPPER.writeValueAsBytes(bean);
        } catch (Exception e) {
            log.error("toJsonBytes is error. bean:{}", bean != null ? bean.toString() : null);
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    public static Object byteParseObject(byte[] bytes, Class<?> targetClass) {

        try {
            return MAPPER.readValue(bytes, targetClass);
        } catch (Exception e) {
            log.error("byteParseObject is error.");
            throw new RuntimeException(e.getMessage(), e);
        }


    }

    /**
	 * Bean对象转Json字符串, 美化一下方便阅读...
     * @param bean  obj
     * @return		json.
     */
    public static String toJsonPretty(Object bean) {
        try {
            return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
	 * Json字符串转Bean对象
     * @param json	json str.
     * @param clazz class.
     * @return      bean.
     */
    public static <T> T toBean(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Failed execute to toJson, json: {}", json);
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    /**
	 * Json字符串转Bean列表
     * @param json	json str.
     * @param clazz class.
     * @return		bean.
     */
    public static <T> List<T> toList(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Map jsonToMap(String str) {

        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {};
        HashMap<String, Object> result;
        try {
            result = mapper.readValue(str, typeRef);
        } catch (IOException e) {
            log.error("toJson is error.json:{}", str);
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }

    public static Map<String, Object> jsonToLinkedHashMap(String str) {
        JsonFactory factory = new JsonFactory();
        ObjectMapper mapper = new ObjectMapper(factory);
        TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {};

		LinkedHashMap<String, Object> result;
        try {
            result = mapper.readValue(str, typeRef);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return result;
    }


}
