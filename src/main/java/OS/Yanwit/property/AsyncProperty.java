package OS.Yanwit.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "async.settings.kafka")
public class AsyncProperty {

    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;

}
