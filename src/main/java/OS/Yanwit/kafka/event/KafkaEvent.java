package OS.Yanwit.kafka.event;

import OS.Yanwit.model.OperationType;
import lombok.Data;

@Data
public abstract class KafkaEvent {
    private OperationType operationType;
}
