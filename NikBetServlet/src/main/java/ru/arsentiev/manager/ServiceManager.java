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
                RepositoryManager.getUserRepository(), ValidationManager.getUpdateUserValidator());
        gameService = new GameService(RepositoryManager.getGameRepository(), RepositoryManager.getPredictionRepository(),
                RepositoryManager.getUserRepository(), MapperManager.getGameMapper());
        teamService = new TeamService(RepositoryManager.getTeamRepository(), MapperManager.getTeamMapper());
        predictionService = new PredictionService(MapperManager.getPredictionMapper(), RepositoryManager.getPredictionRepository(),
                RepositoryManager.getGameRepository(), RepositoryManager.getUserRepository());
    }

}
