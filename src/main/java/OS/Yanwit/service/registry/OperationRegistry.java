package OS.Yanwit.service.registry;

import OS.Yanwit.model.OperationType;
import OS.Yanwit.service.operation.Operation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OperationRegistry<O extends Operation<?>>{
    private final Map<OperationType, O> operationMap;

    public OperationRegistry(List<O> operations) {
        operationMap = operations.stream()
                .collect(Collectors.toMap(
                        Operation::getOperationType,
                        Function.identity()
                ));
    }

    public O getOperation(OperationType type) {
        return operationMap.get(type);
    }
}
