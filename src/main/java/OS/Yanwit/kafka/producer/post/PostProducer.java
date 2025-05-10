package OS.Yanwit.kafka.producer.post;

import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.kafka.producer.AbstractKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PostProducer extends AbstractKafkaProducer<PostEvent> {

    @Value("${spring.data.kafka.topics.topic-settings.posts.name}")
    private String channelTopic;

    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        Map<String, NewTopic> topicMap) {
        super(kafkaTemplate, topicMap);
    }

    @Override
    public String getTopic() {
        return channelTopic;
    }
}
