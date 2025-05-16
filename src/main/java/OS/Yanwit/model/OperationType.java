package OS.Yanwit.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {
    ADD_POST, DELETE_POST,
    ADD_COMMENT, DELETE_COMMENT,
    ADD_SUBSCRIPTION, DELETE_SUBSCRIPTION,
    ADD_LIKE, DELETE_LIKE;

    @JsonCreator
    public static OperationType fromString(String value) {
        return OperationType.valueOf(value.toUpperCase());
    }
}
