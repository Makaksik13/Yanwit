package OS.Yanwit.kafka.event;

import OS.Yanwit.model.OperationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class KafkaEvent {
    private OperationType operationType;
}
