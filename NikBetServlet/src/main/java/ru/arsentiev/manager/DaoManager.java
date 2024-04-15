package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.singleton.connection.ConnectionManager;
import ru.arsentiev.repository.*;
import ru.arsentiev.singleton.query.UserQueryCreator;

@UtilityClass
public class DaoManager {
    @Getter
    private static final UserDao userDao;
    @Getter
    private static final TeamDao teamDao;
    @Getter
    private static final GameDao gameDao;
    @Getter
    private static final PredictionDao predictionDao;
    @Getter
    private static final UserExistsDao userExistsDao;

    static {
        userDao = new UserDao(ConnectionManager.getInstance(), UserQueryCreator.getInstance());
        userExistsDao = new UserExistsDao(ConnectionManager.getInstance());
        teamDao = new TeamDao(ConnectionManager.getInstance());
        gameDao = new GameDao(ConnectionManager.getInstance(), teamDao);
        predictionDao = new PredictionDao(ConnectionManager.getInstance(), gameDao, userDao);
    }
}
