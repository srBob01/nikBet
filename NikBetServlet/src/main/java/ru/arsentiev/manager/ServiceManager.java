package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.processing.password.PasswordHashed;
import ru.arsentiev.service.GameService;
import ru.arsentiev.service.PredictionService;
import ru.arsentiev.service.TeamService;
import ru.arsentiev.service.UserService;

@UtilityClass
public class ServiceManager {
    @Getter
    private static final UserService userService;
    @Getter
    private static final GameService gameService;
    @Getter
    private static final TeamService teamService;
    @Getter
    private static final PredictionService predictionService;

    static {
        userService = new UserService(MapperManager.getUserMapper(), new PasswordHashed(),
                DaoManager.getUserDao(), ValidationManager.getUpdateUserValidator());
        gameService = new GameService(DaoManager.getGameDao(), DaoManager.getPredictionDao(),
                DaoManager.getUserDao(), MapperManager.getGameMapper());
        teamService = new TeamService(DaoManager.getTeamDao(), MapperManager.getTeamMapper());
        predictionService = new PredictionService(MapperManager.getPredictionMapper(), DaoManager.getPredictionDao(),
                DaoManager.getGameDao(), DaoManager.getUserDao());
    }

}
