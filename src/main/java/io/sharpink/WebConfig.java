package io.sharpink;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

  @Autowired
  private EndpointLoggingInterceptor endpointLoggingInterceptor;

  /**
   * Gère les pb de CORS de façon globale
   */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedMethods("*");
  }

  /**
   * Intercepte toutes les requêtes entrantes pour les loguer
   */
  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(endpointLoggingInterceptor);
  }

  /**
   * Configuration custom pour Jackson
   */
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setObjectMapper(new ObjectMapper()
      .findAndRegisterModules()
      .setSerializationInclusion(JsonInclude.Include.NON_NULL));
    converters.add(converter);
  }
}


