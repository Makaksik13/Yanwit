package OS.Yanwit.kafka.consumer;

import OS.Yanwit.kafka.event.KafkaEvent;
import OS.Yanwit.service.operation.Operation;
import OS.Yanwit.service.registry.OperationRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
@RequiredArgsConstructor
public abstract class KafkaMultiFunctionalConsumer<T extends KafkaEvent> implements KafkaConsumer<T>{
    private final OperationRegistry<Operation<T>> operationRegistry;

    public void consume(T event, Acknowledgment ack){
        log.info("Received new {}: {}", event.getClass().getSimpleName(), event);

        Operation<T> op = operationRegistry.getOperation(event.getOperationType());
        if (op != null) {
            op.execute(event);
        } else {
            throw new UnsupportedOperationException("Unknown operation");
        }

        ack.acknowledge();
    }
}
