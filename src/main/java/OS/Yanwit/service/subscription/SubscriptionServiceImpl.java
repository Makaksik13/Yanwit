package OS.Yanwit.service.subscription;

import OS.Yanwit.model.dto.SubscriptionRequestDto;
import OS.Yanwit.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    @Override
    @Transactional
    public void followUser(SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionRepository.followUser(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());
    }

    @Override
    @Transactional
    public void unfollowUser(SubscriptionRequestDto subscriptionRequestDto) {
        subscriptionRepository.unfollowUser(subscriptionRequestDto.getFollowerId(), subscriptionRequestDto.getFolloweeId());
    }
}
