package OS.Yanwit.service.registry;

import OS.Yanwit.model.OperationType;
import OS.Yanwit.service.operation.PostOperation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PostOperationRegistry {
    private final Map<OperationType, PostOperation> operationMap;

    public PostOperationRegistry(List<PostOperation> operations) {
        this.operationMap = operations.stream()
                .collect(Collectors.toMap(
                        PostOperation::getOperationType,
                        Function.identity()
                ));
    }

    public PostOperation getOperation(OperationType type) {
        return operationMap.get(type);
    }
}
