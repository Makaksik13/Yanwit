package OS.Yanwit.kafka.event.like;

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
public class PostLikeEvent extends KafkaEvent {

    private Long postId;
    private Long authorId;
    private Long likeId;
}
