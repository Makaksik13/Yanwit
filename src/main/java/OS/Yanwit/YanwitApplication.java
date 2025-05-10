package OS.Yanwit;

import OS.Yanwit.config.redis.RedisCacheConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

@OpenAPIDefinition(
		info = @Info(
				title = "Social network Yanwit",
				description = "Project on the discipline \"Operating systems\"", version = "1.0.0",
				contact = @Contact(
						name = "Maxim Sukhanov",
						email = "maksimka_13.05.2004@mail.ru"
				)
		)
)
@SpringBootApplication
@EnableCaching
@ConfigurationPropertiesScan("OS.Yanwit.property")
@EnableRetry
@EnableAsync(proxyTargetClass = true)
@EnableRedisRepositories(keyspaceConfiguration = RedisCacheConfig.RedisKeyspaceConfiguration.class)
public class YanwitApplication {

	public static void main(String[] args) {
		SpringApplication.run(YanwitApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		return objectMapper;
	}
}
