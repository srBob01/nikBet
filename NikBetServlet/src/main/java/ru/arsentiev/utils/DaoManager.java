package ru.arsentiev.utils;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.repository.*;

@UtilityClass
public class DaoManager {
    private static final ConnectionManager connectionManager;
    @Getter
    private static final UserDao userDao;
    @Getter
    private static final TeamDao teamDao;
    @Getter
    private static final GameDao gameDao;
    @Getter
    private static final PredictionDao predictionDao;

    static {
        connectionManager = new ConnectionManager();
        userDao = new UserDao(connectionManager);
        teamDao = new TeamDao(connectionManager);
        gameDao = new GameDao(connectionManager, teamDao);
        predictionDao = new PredictionDao(connectionManager, gameDao, userDao);
    }
}
