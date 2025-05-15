package OS.Yanwit.service.registry;

import OS.Yanwit.service.operation.post.PostOperation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostOperationRegistry extends AbstractOperationRegistry<PostOperation> {

    public PostOperationRegistry(List<PostOperation> operations) {
        super(operations);
    }
}
