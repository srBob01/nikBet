package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.processing.connection.ConnectionGetter;

@UtilityClass
public class ConnectionManager {
    @Getter
    private static final ConnectionGetter connectionGetter;

    static {
        connectionGetter = new ConnectionGetter();
    }
}
