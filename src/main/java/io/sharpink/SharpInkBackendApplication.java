package io.sharpink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SharpInkBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SharpInkBackendApplication.class, args);
	}
}
