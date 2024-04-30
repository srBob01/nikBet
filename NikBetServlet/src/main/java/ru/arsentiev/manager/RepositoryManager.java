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
    @Getter
    private static final UserExistsRepository userExistRepository;

    static {
        userRepository = new UserRepository(ConnectionManager.getConnectionGetter(), QueryCreatorManager.getUserQueryCreator());
        userExistRepository = new UserExistsRepository(ConnectionManager.getConnectionGetter());
        teamRepository = new TeamRepository(ConnectionManager.getConnectionGetter());
        gameRepository = new GameRepository(ConnectionManager.getConnectionGetter(), teamRepository, QueryCreatorManager.getGameQueryCreator());
        predictionRepository = new PredictionRepository(ConnectionManager.getConnectionGetter(), gameRepository, userRepository);
    }
}
