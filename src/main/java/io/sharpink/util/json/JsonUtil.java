package io.sharpink.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class JsonUtil {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static <T> T fromJson(String json, Class<T> classz) {
    try {
      return MAPPER.readValue(json, classz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String toJson(Object object) {
    try {
      return MAPPER.writeValueAsString(object);
    } catch(JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static ObjectMapper getMapper() {
    return MAPPER;
  }
}
