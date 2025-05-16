package OS.Yanwit.kafka.consumer.subscription;

import OS.Yanwit.kafka.consumer.KafkaMultiFunctionalConsumer;
import OS.Yanwit.kafka.event.subscription.SubscriptionEvent;
import OS.Yanwit.service.operation.Operation;
import OS.Yanwit.service.registry.OperationRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubscriptionConsumer extends KafkaMultiFunctionalConsumer<SubscriptionEvent> {

    public SubscriptionConsumer(OperationRegistry<Operation<SubscriptionEvent>> operationRegistry) {
        super(operationRegistry);
    }

    @Override
    @KafkaListener(topics = "${spring.data.kafka.topics.topic-settings.subscription.name}", groupId = "${spring.data.kafka.group-id}")
    public void consume(SubscriptionEvent event, Acknowledgment ack) {
        super.consume(event, ack);
    }
}
