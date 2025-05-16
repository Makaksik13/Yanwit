package OS.Yanwit.kafka.consumer.post;

import OS.Yanwit.kafka.consumer.KafkaMultiFunctionalConsumer;
import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.service.operation.Operation;
import OS.Yanwit.service.registry.OperationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component

public class PostConsumer extends KafkaMultiFunctionalConsumer<PostEvent> {

    public PostConsumer(OperationRegistry<Operation<PostEvent>> operationRegistry) {
        super(operationRegistry);
    }

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.posts.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload PostEvent event, Acknowledgment ack) {
        super.consume(event, ack);
    }
}
