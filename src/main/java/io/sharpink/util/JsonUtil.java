package io.sharpink.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;

@Slf4j
public class JsonUtil {
  private static final ObjectMapper MAPPER = configureMapper();


  public static <T> T fromJson(String json, Class<T> classz) {
    try {
      return MAPPER.readValue(json, classz);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> List<T> fromJsonArray(String json, Class<T> classz) {
    try {
      return MAPPER.readValue(json, MAPPER.getTypeFactory().constructCollectionType(List.class, classz));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static String toJson(Object object) {
    try {
      return MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static ObjectMapper getMapper() {
    return MAPPER;
  }

  private static ObjectMapper configureMapper() {
    return new ObjectMapper().findAndRegisterModules()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL)
      .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
      .registerModule(new JavaTimeModule().addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(ISO_DATE_TIME)));
  }
}
