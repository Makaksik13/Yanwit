package OS.Yanwit.kafka.producer;

import OS.Yanwit.kafka.event.KafkaEvent;

public interface KafkaProducer<T extends KafkaEvent> {
    void produce(T event);

    void produce(T event, Runnable runnable);
}
