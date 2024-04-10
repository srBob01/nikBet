package ru.arsentiev.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.connection.ConnectionManager;
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

    static {
        userDao = new UserDao(ConnectionManager.getInstance());
        teamDao = new TeamDao(ConnectionManager.getInstance());
        gameDao = new GameDao(ConnectionManager.getInstance(), teamDao);
        predictionDao = new PredictionDao(ConnectionManager.getInstance(), gameDao, userDao);
    }
}
