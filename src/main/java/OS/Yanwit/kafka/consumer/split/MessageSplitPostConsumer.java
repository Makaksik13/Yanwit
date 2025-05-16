package OS.Yanwit.kafka.consumer.split;

import OS.Yanwit.kafka.consumer.KafkaConsumer;
import OS.Yanwit.kafka.event.post.PostEvent;
import OS.Yanwit.kafka.producer.post.PostProducer;
import OS.Yanwit.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MessageSplitPostConsumer implements KafkaConsumer<PostEvent> {

    @Value("${spring.data.kafka.topics.split-batch-size}")
    private int splitBatchSize;

    private final PostProducer postProducer;
    private final PostMapper postMapper;

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.split.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(PostEvent event, Acknowledgment ack) {
        List<List<Long>> partitions = ListUtils.partition(event.getFollowersIds(), splitBatchSize);
        partitions.forEach(sublist->{
            PostEvent postEvent = new PostEvent(
                    event.getPostId(),
                    event.getAuthorId(),
                    event.getContent(),
                    sublist,
                    event.getPublishedAt());
            postEvent.setOperationType(event.getOperationType());
            postProducer.produce(postEvent);
        });
    }
}
