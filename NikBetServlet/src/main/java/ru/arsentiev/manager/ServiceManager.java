package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.processing.password.PasswordHashed;
import ru.arsentiev.servicelayer.service.GameService;
import ru.arsentiev.servicelayer.service.PredictionService;
import ru.arsentiev.servicelayer.service.TeamService;
import ru.arsentiev.servicelayer.service.UserService;

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
                RepositoryManager.getUserRepository(), ValidationManager.getUserValidator());
        gameService = new GameService(RepositoryManager.getGameRepository(), RepositoryManager.getPredictionRepository(),
                RepositoryManager.getUserRepository(), MapperManager.getGameMapper());
        teamService = new TeamService(RepositoryManager.getTeamRepository(), MapperManager.getTeamMapper());
        predictionService = new PredictionService(MapperManager.getPredictionMapper(), RepositoryManager.getPredictionRepository(),
                RepositoryManager.getGameRepository(), RepositoryManager.getUserRepository());
    }

}
