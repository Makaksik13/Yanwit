package OS.Yanwit.property;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "spring.data.kafka.topics")
public class KafkaChannelProperty {
    private final Integer splitBatchSize;
    private final Short defaultReplication;
    private final Integer defaultPartition;

    private final Map<String, Channel> topicSettings;

    @Data
    @NoArgsConstructor
    public static class Channel {

        private String name;
        private Integer partition;
        private Short replication;
    }
}
