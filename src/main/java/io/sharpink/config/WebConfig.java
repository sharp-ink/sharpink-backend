package io.sharpink.config;

import io.sharpink.util.JsonUtil;
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
     * Handle CORS issues globally
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("*");
    }

    /**
     * Intercept all incoming requests to log them
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
        converter.setObjectMapper(JsonUtil.getMapper());
        converters.add(converter);
    }
}


