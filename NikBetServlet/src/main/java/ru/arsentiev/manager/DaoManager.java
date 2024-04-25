package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.repository.*;

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
        userDao = new UserDao(ConnectionManager.getConnectionGetter(), QueryCreatorManager.getUserQueryCreator());
        userExistsDao = new UserExistsDao(ConnectionManager.getConnectionGetter());
        teamDao = new TeamDao(ConnectionManager.getConnectionGetter());
        gameDao = new GameDao(ConnectionManager.getConnectionGetter(), teamDao, QueryCreatorManager.getGameQueryCreator());
        predictionDao = new PredictionDao(ConnectionManager.getConnectionGetter(), gameDao, userDao);
    }
}
