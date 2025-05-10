package OS.Yanwit.config.async;

import OS.Yanwit.property.AsyncProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncConfig {

    private final AsyncProperty asyncProperty;

    @Bean
    public Executor kafkaThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(asyncProperty.getCorePoolSize());
        executor.setMaxPoolSize(asyncProperty.getMaxPoolSize());
        executor.setQueueCapacity(asyncProperty.getQueueCapacity());
        executor.setThreadNamePrefix("KafkaAsyncThread-");
        executor.initialize();
        return executor;
    }
}
