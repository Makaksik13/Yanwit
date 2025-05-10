package OS.Yanwit.service.subscription;

import OS.Yanwit.model.dto.SubscriptionRequestDto;

public interface SubscriptionService {

    void followUser(SubscriptionRequestDto subscriptionRequestDto);

    void unfollowUser(SubscriptionRequestDto subscriptionRequestDto);
}
