package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.game.GameViewCompletedDto;
import ru.arsentiev.dto.game.GameViewInProgressDto;
import ru.arsentiev.dto.game.GameViewScheduledDto;
import ru.arsentiev.manager.DaoManager;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.repository.GameDao;

import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameService {
    private static final GameService INSTANCE = new GameService();
    private static final GameDao gameDao = DaoManager.getGameDao();
    private static final GameMapper gameMapper = GameMapper.getInstance();

    public static GameService getInstance() {
        return INSTANCE;
    }

    public List<GameViewInProgressDto> selectGameInProgress() {
        return gameDao.selectAllGameInProgress().stream()
                .map(gameMapper::mapViewInProgress)
                .collect(toList());
    }

    public List<GameViewScheduledDto> selectGameScheduled() {
        return gameDao.selectAllGameScheduled().stream()
                .map(gameMapper::mapViewScheduled)
                .collect(toList());
    }

    public List<GameViewCompletedDto> selectGameCompleted() {
        return gameDao.selectAllGameCompleted().stream()
                .map(gameMapper::mapViewCompleted)
                .collect(toList());
    }
}
