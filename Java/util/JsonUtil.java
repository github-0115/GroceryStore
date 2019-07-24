package uyun.show.server.domain.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
	public static final ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper();
		// 反序列化时，忽略未知属性
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static <T> T readJson(String jsonString, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {
		if (jsonString == null || jsonString.isEmpty()) {
			return null;
		}
		return objectMapper.readValue(jsonString, clazz);
	}

	public static String encodeJson(Object o) throws JsonParseException, JsonMappingException, IOException {
		if (o == null)
			return null;
		return objectMapper.writeValueAsString(o);
	}

	/**
	 * 获取泛型的Collection Type
	 * 
	 * @param jsonStr json字符串
	 * @param collectionClass 泛型的Collection
	 * @param elementClasses 元素类型
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	public static <T> T readJson(String jsonStr, Class<?> collectionClass, Class<?>... elementClasses)
			throws JsonParseException, JsonMappingException, IOException {
		JavaType javaType = objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
		return objectMapper.readValue(jsonStr, javaType);
	}
}
