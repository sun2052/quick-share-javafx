package org.byteinfo.quickshare.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSON {
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static String stringify(Object object) {
		try {
			return MAPPER.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> T parse(String json, Class<T> clazz) {
		try {
			return MAPPER.readValue(json, clazz);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
