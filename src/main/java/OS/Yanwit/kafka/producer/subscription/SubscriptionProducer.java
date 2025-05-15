package OS.Yanwit.kafka.producer.subscription;

import OS.Yanwit.kafka.event.subscription.SubscriptionEvent;
import OS.Yanwit.kafka.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SubscriptionProducer extends AbstractKafkaProducer<SubscriptionEvent> {

    @Value("${spring.data.kafka.topics.topic-settings.subscription.name}")
    private String channelTopic;

    public SubscriptionProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                Map<String, NewTopic> topicMap) {
        super(kafkaTemplate, topicMap);
    }

    @Override
    public String getTopic() {
        return channelTopic;
    }
}
