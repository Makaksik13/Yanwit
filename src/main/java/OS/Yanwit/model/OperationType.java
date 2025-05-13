package OS.Yanwit.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum OperationType {
    ADD, DELETE, UPDATE;

    @JsonCreator
    public static OperationType fromString(String value) {
        return OperationType.valueOf(value.toUpperCase());
    }
}
