package OS.Yanwit.config.async;

import OS.Yanwit.property.RedisAsyncProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class AsyncCacheConfig {

    private final RedisAsyncProperty redisAsyncProperty;

    private static final Map<String, String> EXECUTOR_CONFIGS = Map.of(
            "posts", "PostCacheAsyncThread-",
            "comments", "CommentsCacheAsyncThread-",
            "authors", "AuthorsCacheAsyncThread-",
            "feed", "FeedCacheAsyncThread-"
    );

    private Executor createExecutor(String configKey) {
        var config = redisAsyncProperty.getSettings().get(configKey);

        if (config == null) {
            throw new IllegalArgumentException("Settings for cache " + configKey + " not found");
        }

        if (!EXECUTOR_CONFIGS.containsKey(configKey)) {
            throw new IllegalArgumentException("Invalid config key: " + configKey);
        }
        String threadPrefix = EXECUTOR_CONFIGS.get(configKey);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.getCorePoolSize());
        executor.setMaxPoolSize(config.getMaxPoolSize());
        executor.setQueueCapacity(config.getQueueCapacity());
        executor.setThreadNamePrefix(threadPrefix);
        executor.initialize();
        return executor;
    }

    @Bean
    public Executor postsCacheTaskExecutor() {
        return createExecutor("posts");
    }

    @Bean
    public Executor commentsCacheTaskExecutor() {
        return createExecutor("comments");
    }

    @Bean
    public Executor authorsCacheTaskExecutor() {
        return createExecutor("authors");
    }

    @Bean
    public Executor feedCacheTaskExecutor() {
        return createExecutor("feed");
    }
}

