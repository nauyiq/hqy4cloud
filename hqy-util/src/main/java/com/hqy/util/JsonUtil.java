package com.hqy.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description JSON实用类
 * @author qy
 * @date 2021-07-08 17:10
 */
@Slf4j
public class JsonUtil {

//	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);

	private final static ObjectMapper MAPPER = new ObjectMapper();

	static {
		MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);


		//MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true) ;
		//MAPPER.setSerializationInclusion(Include.NON_NULL); //属性为NULL 不序列化
		
		//使用标准  STANDER_DATETIME_TEMPLATE = "yyyy-MM-dd HH:mm:ss" 作为时间对象序列化格式
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MAPPER.setDateFormat(dateFormat);
		
	}
	
	public static void registerModule(Module module) {
		MAPPER.registerModule(module);
	}

	/**
	 * @description Bean对象转Json字符串
	 * @param bean
	 * @return
	 */
	public static String toJson(Object bean) {
		try {
		    if(bean == null) {
		        return "{}";
		    }
			//MAPPER.setSerializationInclusion(Include.NON_EMPTY);
			return MAPPER.writeValueAsString(bean);
		} catch (Exception e) {
			log.error("toJson is error.bean:{}",bean != null?bean.toString():null);
			throw new RuntimeException(e.getMessage(),e);
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
	 * @description Bean对象转Json字符串
	 * @param bean
	 * @return
	 */
	public static String toJson2(Object bean) {
		try {
			return MAPPER.writeValueAsString(bean);
		} catch (Exception e) {
			log.error("toJson is error.bean:{}",bean != null?bean.toString():null);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	/**
	 * @description Bean对象转Json字符串,美化一下方便阅读...
	 * @param bean
	 * @return
	 */
	public static String toJsonPretty(Object bean) {
		try {
			return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	

	  /**
     * 将一个对象转换成json格式的string
     * @return
     */
    public static String toJsonP(final String callback, final Object data) {

        try {
            return new ObjectMapper().writeValueAsString(new JSONPObject(callback, data));
        } catch (final Exception e) {
        	throw new RuntimeException(e.getMessage(),e);
        }
    }
    
	
	/**
	 * @description Json字符串转Bean对象
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T toBean(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, clazz);
		} catch (Exception e) {
			log.error("toJson is error.json:{}",json);
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	
	public static <T> T toBean(final String json, final TypeReference<T> a_Class) {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        try {
            final T uniJSON = mapper.readValue(json, a_Class);
            return uniJSON;
        } catch (final Exception e) {
			log.error("toJson is error.json:{}",json);
        	throw new RuntimeException(e.getMessage(),e);
        }
    }
	
	


	/**
	 * @description Json字符串转Bean列表
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> toList(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, clazz));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}

	@SuppressWarnings("rawtypes")
	public static Map  jsonToMap(String str)  {

		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);
		TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
		};
		HashMap<String, Object> result = null;
		try {
			result = mapper.readValue(str, typeRef);
		} catch (IOException e) {
			log.error("toJson is error.json:{}",str);
			throw new RuntimeException(e.getMessage(),e);
		}
		return result;
	}
	
	public static Map<String, Object> jsonToLinkedHashMap(String str)  {

		JsonFactory factory = new JsonFactory();
		ObjectMapper mapper = new ObjectMapper(factory);
		TypeReference<LinkedHashMap<String, Object>> typeRef = new TypeReference<LinkedHashMap<String, Object>>() {
		};
		LinkedHashMap<String, Object> result = null;
		try {
			result = mapper.readValue(str, typeRef);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
		return result;
	}

	
}
