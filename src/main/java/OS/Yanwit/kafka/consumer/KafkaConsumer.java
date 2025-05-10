package OS.Yanwit.kafka.consumer;

import OS.Yanwit.kafka.event.KafkaEvent;
import org.springframework.kafka.support.Acknowledgment;

public interface KafkaConsumer<T extends KafkaEvent> {

    void consume(T event, Acknowledgment ack);
}
