package OS.Yanwit.mapper;

import OS.Yanwit.kafka.event.subscription.SubscriptionEvent;
import OS.Yanwit.model.dto.SubscriptionRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubscriptionMapper {

    SubscriptionEvent toEvent(SubscriptionRequestDto subscriptionRequestDto);
}
