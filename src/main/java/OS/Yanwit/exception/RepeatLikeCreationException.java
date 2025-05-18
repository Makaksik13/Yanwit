package OS.Yanwit.exception;

public class RepeatLikeCreationException extends RuntimeException {
    public RepeatLikeCreationException(long postId, long userId) {
        super(String.format("Repeated attempt to create a like " +
                "on the post with id = %d by a user with id = %d", postId, userId));
    }
}
