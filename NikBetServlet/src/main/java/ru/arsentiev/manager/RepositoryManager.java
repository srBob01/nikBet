package ru.arsentiev.manager;

import lombok.Getter;
import lombok.experimental.UtilityClass;
import ru.arsentiev.repository.*;

@UtilityClass
public class RepositoryManager {
    @Getter
    private static final UserRepository userRepository;
    @Getter
    private static final TeamRepository teamRepository;
    @Getter
    private static final GameRepository gameRepository;
    @Getter
    private static final PredictionRepository predictionRepository;

    static {
        userRepository = new UserRepository(ConnectionManager.getMyConnectionGetter(), QueryCreatorManager.getUserQueryCreator());
        teamRepository = new TeamRepository(ConnectionManager.getMyConnectionGetter());
        gameRepository = new GameRepository(ConnectionManager.getMyConnectionGetter(), teamRepository, QueryCreatorManager.getGameQueryCreator());
        predictionRepository = new PredictionRepository(ConnectionManager.getMyConnectionGetter(), gameRepository, userRepository);
    }
}
