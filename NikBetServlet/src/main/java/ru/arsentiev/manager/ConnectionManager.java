package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.processing.connection.ConnectionGetter;
import ru.arsentiev.processing.connection.MyConnectionGetter;

@UtilityClass
public class ConnectionManager {
    @Getter
    private static final ConnectionGetter myConnectionGetter;

    static {
        myConnectionGetter = new MyConnectionGetter();
    }
}
