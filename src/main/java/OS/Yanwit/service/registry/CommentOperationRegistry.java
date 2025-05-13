package OS.Yanwit.service.registry;

import OS.Yanwit.model.OperationType;
import OS.Yanwit.service.operation.comment.CommentOperation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CommentOperationRegistry {
    private final Map<OperationType, CommentOperation> operationMap;

    public CommentOperationRegistry(List<CommentOperation> operations) {
        operationMap = operations.stream()
                .collect(Collectors.toMap(
                        CommentOperation::getOperationType,
                        Function.identity()
                ));
    }

    public CommentOperation getOperation(OperationType type) {
        return operationMap.get(type);
    }
}
