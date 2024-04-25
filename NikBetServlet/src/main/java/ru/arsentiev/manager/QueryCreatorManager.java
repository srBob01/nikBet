package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.processing.query.GameQueryCreator;
import ru.arsentiev.processing.query.UserQueryCreator;

@UtilityClass
public class QueryCreatorManager {
    @Getter
    private static final UserQueryCreator userQueryCreator;
    @Getter
    private static final GameQueryCreator gameQueryCreator;

    static {
        userQueryCreator = new UserQueryCreator();
        gameQueryCreator = new GameQueryCreator();
    }
}
