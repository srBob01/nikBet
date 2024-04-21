package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.game.controller.GameCompletedControllerDto;
import ru.arsentiev.dto.game.controller.GameParametersControllerDto;
import ru.arsentiev.dto.game.controller.GameProgressControllerDto;
import ru.arsentiev.dto.game.controller.GameScheduledControllerDto;
import ru.arsentiev.dto.game.view.GameParametersViewDto;
import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameStatus;
import ru.arsentiev.manager.DaoManager;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.repository.GameDao;
import ru.arsentiev.service.entity.game.TripleListOfGameControllerDto;
import ru.arsentiev.singleton.query.entity.CompletedGameFields;

import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameService {
    private static final GameService INSTANCE = new GameService();
    private final GameDao gameDao = DaoManager.getGameDao();
    private final GameMapper gameMapper = GameMapper.getInstance();

    public static GameService getInstance() {
        return INSTANCE;
    }

    public List<GameProgressControllerDto> selectGameInProgressLimit() {
        return gameDao.selectLimitGameInProgress().stream()
                .map(gameMapper::mapControllerInProgressGameToController)
                .collect(toList());
    }

    public List<GameScheduledControllerDto> selectGameScheduledLimit() {
        return gameDao.selectLimitGameScheduled().stream()
                .map(gameMapper::mapControllerScheduledGameToController)
                .collect(toList());
    }

    public List<GameCompletedControllerDto> selectGameCompletedLimit() {
        return gameDao.selectLimitGameCompleted().stream()
                .map(gameMapper::mapControllerCompletedGameToController)
                .collect(toList());
    }

    public List<GameProgressControllerDto> selectGameInProgressAll() {
        return gameDao.selectAllGameInProgress().stream()
                .map(gameMapper::mapControllerInProgressGameToController)
                .collect(toList());
    }

    public List<GameScheduledControllerDto> selectGameScheduledAll() {
        return gameDao.selectAllGameScheduled().stream()
                .map(gameMapper::mapControllerScheduledGameToController)
                .collect(toList());
    }

    public List<GameCompletedControllerDto> selectGameCompletedAll() {
        return gameDao.selectAllGameCompleted().stream()
                .map(gameMapper::mapControllerCompletedGameToController)
                .collect(toList());
    }

    public TripleListOfGameControllerDto selectGameByParameters(CompletedGameFields completedGameFields,
                                                                GameParametersViewDto gameParametersViewDto) {
        GameParametersControllerDto gameParametersControllerDto = gameMapper.map(gameParametersViewDto);
        Game game = gameMapper.map(gameParametersControllerDto);
        List<Game> gameList = gameDao.selectByParameters(game, completedGameFields);
        List<GameProgressControllerDto> gameViewInProgressDtoList = gameList.stream()
                .filter(game1 -> game1.getStatus().equals(GameStatus.InProgress))
                .map(gameMapper::mapControllerInProgressGameToController)
                .toList();
        List<GameScheduledControllerDto> gameViewScheduledDtoList = gameList.stream()
                .filter(game1 -> game1.getStatus().equals(GameStatus.Scheduled))
                .map(gameMapper::mapControllerScheduledGameToController)
                .toList();
        List<GameCompletedControllerDto> gameViewCompletedDtoList = gameList.stream()
                .filter(game1 -> game1.getStatus().equals(GameStatus.Completed))
                .map(gameMapper::mapControllerCompletedGameToController)
                .toList();
        return TripleListOfGameControllerDto.builder()
                .gameViewScheduledDtoList(gameViewScheduledDtoList)
                .gameViewCompletedDtoList(gameViewCompletedDtoList)
                .gameViewInProgressDtoList(gameViewInProgressDtoList)
                .build();
    }
}
