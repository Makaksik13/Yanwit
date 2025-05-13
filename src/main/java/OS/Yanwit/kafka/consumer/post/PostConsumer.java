package OS.Yanwit.kafka.consumer.post;

import OS.Yanwit.kafka.consumer.KafkaConsumer;
import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import OS.Yanwit.service.operation.post.PostOperation;
import OS.Yanwit.service.registry.PostOperationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostConsumer implements KafkaConsumer<PostEvent> {

    private final FeedCacheService feedCacheService;
    private final PostOperationRegistry postOperationRegistry;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.posts.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload PostEvent event, Acknowledgment ack) {

        log.info("Received new post event {}", event);
        PostOperation op = postOperationRegistry.getOperation(event.getOperationType());
        if (op != null) {
            op.execute(feedCacheService, event);
        } else {
            ack.acknowledge();
            throw new UnsupportedOperationException("Unknown operation");
        }

        ack.acknowledge();
    }
}
