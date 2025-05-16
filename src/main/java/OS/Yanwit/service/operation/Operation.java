package OS.Yanwit.service.operation;

import OS.Yanwit.kafka.event.KafkaEvent;
import OS.Yanwit.model.OperationType;

public interface Operation<T extends KafkaEvent> {
    OperationType getOperationType();

    void execute(T event);
}
