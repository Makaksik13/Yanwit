package OS.Yanwit.service.registry;

import OS.Yanwit.service.operation.comment.CommentOperation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommentOperationRegistry extends AbstractOperationRegistry<CommentOperation> {

    public CommentOperationRegistry(List<CommentOperation> operations) {
        super(operations);
    }
}
