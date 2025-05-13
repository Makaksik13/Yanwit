package OS.Yanwit.kafka.consumer.comment;

import OS.Yanwit.kafka.consumer.KafkaConsumer;
import OS.Yanwit.kafka.event.comment.CommentEvent;
import OS.Yanwit.mapper.CommentMapper;
import OS.Yanwit.redis.cache.service.post.PostCacheService;
import OS.Yanwit.service.operation.comment.CommentOperation;
import OS.Yanwit.service.registry.CommentOperationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentConsumer implements KafkaConsumer<CommentEvent> {

    private final CommentMapper commentMapper;
    private final PostCacheService postCacheService;
    private final CommentOperationRegistry commentOperationRegistry;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.comments.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(@Payload CommentEvent event, Acknowledgment ack) {

        log.info("Received new comment event {}", event);

        CommentOperation op = commentOperationRegistry.getOperation(event.getOperationType());
        if (op != null) {
            op.execute(postCacheService, event);
        } else {
            ack.acknowledge();
            throw new UnsupportedOperationException("Unknown operation");
        }

        ack.acknowledge();
    }
}
