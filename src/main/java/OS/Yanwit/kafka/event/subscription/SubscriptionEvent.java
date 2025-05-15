package OS.Yanwit.kafka.event.subscription;

import OS.Yanwit.kafka.event.KafkaEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@SuperBuilder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionEvent extends KafkaEvent {
    private long followerId;
    private long followeeId;
}
