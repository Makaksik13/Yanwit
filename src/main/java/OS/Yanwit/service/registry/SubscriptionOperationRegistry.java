package OS.Yanwit.service.registry;

import OS.Yanwit.service.operation.subscription.SubscriptionOperation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionOperationRegistry extends AbstractOperationRegistry<SubscriptionOperation>{

    public SubscriptionOperationRegistry(List<SubscriptionOperation> operations) {
        super(operations);
    }
}
