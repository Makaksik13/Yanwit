package OS.Yanwit.kafka.consumer.subscription;

import OS.Yanwit.kafka.consumer.KafkaConsumer;
import OS.Yanwit.kafka.event.subscription.SubscriptionEvent;
import OS.Yanwit.redis.cache.service.feed.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionConsumer implements KafkaConsumer<SubscriptionEvent> {

    private final FeedCacheService feedCacheService;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.subscription.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(SubscriptionEvent event, Acknowledgment ack) {

        log.info("Received new subscription event {}", event);

        feedCacheService.AddPostsFromAuthorToUserFeed(event.getFollowerId(), event.getFolloweeId());

        ack.acknowledge();
    }
}
