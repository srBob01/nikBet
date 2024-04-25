package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.game.controller.*;
import ru.arsentiev.dto.user.controller.UserMoneyControllerDto;
import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameStatus;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.manager.DaoManager;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.repository.GameDao;
import ru.arsentiev.repository.PredictionDao;
import ru.arsentiev.repository.UserDao;
import ru.arsentiev.service.entity.game.TripleListOfGameControllerDto;
import ru.arsentiev.singleton.query.entity.CompletedGameFields;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameService {
    private static final GameService INSTANCE = new GameService();
    private final GameDao gameDao = DaoManager.getGameDao();
    private final PredictionDao predictionDao = DaoManager.getPredictionDao();
    private final UserDao userDao = DaoManager.getUserDao();
    private final GameMapper gameMapper = GameMapper.getInstance();

    public static GameService getInstance() {
        return INSTANCE;
    }

    public List<GameProgressControllerDto> selectGameInProgressLimit() {
        return gameDao.selectLimitGameInProgress().stream()
                .map(gameMapper::mapGameToControllerInProgress)
                .collect(toList());
    }

    public List<GameScheduledControllerDto> selectGameScheduledLimit() {
        return gameDao.selectLimitGameScheduled().stream()
                .map(gameMapper::mapGameToControllerScheduled)
                .collect(toList());
    }

    public List<GameCompletedControllerDto> selectGameCompletedLimit() {
        return gameDao.selectLimitGameCompleted().stream()
                .map(gameMapper::mapGameToControllerCompleted)
                .collect(toList());
    }

    public List<GameProgressControllerDto> selectGameInProgressAll() {
        return gameDao.selectAllGameInProgress().stream()
                .map(gameMapper::mapGameToControllerInProgress)
                .collect(toList());
    }

    public List<GameScheduledControllerDto> selectGameScheduledAll() {
        return gameDao.selectAllGameScheduled().stream()
                .map(gameMapper::mapGameToControllerScheduled)
                .collect(toList());
    }

    public List<GameCompletedControllerDto> selectGameCompletedAll() {
        return gameDao.selectAllGameCompleted().stream()
                .map(gameMapper::mapGameToControllerCompleted)
                .collect(toList());
    }

    public List<GameScheduledControllerDto> selectHotGame() {
        return gameDao.selectHotGameScheduled().stream()
                .map(gameMapper::mapGameToControllerScheduled)
                .toList();
    }

    public TripleListOfGameControllerDto selectGameByParameters(CompletedGameFields completedGameFields,
                                                                GameParametersControllerDto gameParametersControllerDto) {
        Game game = gameMapper.map(gameParametersControllerDto);
        List<Game> gameList = gameDao.selectByParameters(game, completedGameFields);
        List<GameProgressControllerDto> gameViewInProgressDtoList = gameList.stream()
                .filter(game1 -> game1.getStatus().equals(GameStatus.InProgress))
                .map(gameMapper::mapGameToControllerInProgress)
                .toList();
        List<GameScheduledControllerDto> gameViewScheduledDtoList = gameList.stream()
                .filter(game1 -> game1.getStatus().equals(GameStatus.Scheduled))
                .map(gameMapper::mapGameToControllerScheduled)
                .toList();
        List<GameCompletedControllerDto> gameViewCompletedDtoList = gameList.stream()
                .filter(game1 -> game1.getStatus().equals(GameStatus.Completed))
                .map(gameMapper::mapGameToControllerCompleted)
                .toList();
        return TripleListOfGameControllerDto.builder()
                .gameViewScheduledDtoList(gameViewScheduledDtoList)
                .gameViewCompletedDtoList(gameViewCompletedDtoList)
                .gameViewInProgressDtoList(gameViewInProgressDtoList)
                .build();
    }

    public boolean addNewGame(GameAdminScheduledControllerDto gameAdminScheduledControllerDto) {
        if (gameAdminScheduledControllerDto.idGuestTeam() == gameAdminScheduledControllerDto.idHomeTeam()
            || gameAdminScheduledControllerDto.gameDate().isBefore(LocalDateTime.now())) {
            return false;
        }
        Game game = gameMapper.mapAdminScheduledControllerToGame(gameAdminScheduledControllerDto);
        gameDao.insert(game);
        return true;
    }

    public boolean startGame(Long idGame) {
        return gameDao.startGame(idGame);
    }

    public boolean updateDescriptionGame(GameAdminProgressControllerDto gameAdminProgressControllerDto) {
        Game game = gameMapper.mapAdminProgressControllerToGame(gameAdminProgressControllerDto);
        return gameDao.updateDescriptionGame(game);
    }

    public boolean startSecondHalf(Long idGame) {
        return gameDao.startSecondHalf(idGame);
    }

    public boolean endGame(GameAdminCompletedControllerDto gameAdminCompletedControllerDto) {
        Game game = gameMapper.mapAdminCompletedControllerToGame(gameAdminCompletedControllerDto);
        if (!gameDao.endGame(game)) {
            return false;
        }
        List<Prediction> predictions = predictionDao.selectByGameId(gameAdminCompletedControllerDto.idGame());
        predictions.stream()
                .filter(prediction -> prediction.getPrediction().compareTo(game.getResult()) == 0)
                .forEach(prediction -> {
                    BigDecimal winning = prediction.getSumma().multiply(
                                    BigDecimal.valueOf(prediction.getCoefficient()))
                            .setScale(2, RoundingMode.HALF_UP);
                    UserMoneyControllerDto userMoneyControllerDto = UserMoneyControllerDto.builder()
                            .idUser(prediction.getUser().getIdUser())
                            .summa(winning)
                            .build();
                    userDao.depositMoneyById(userMoneyControllerDto);
                });
        List<Long> listIdPredictions = predictions.stream()
                .map(Prediction::getIdPrediction)
                .toList();
        predictionDao.updatePredictionStatusOfList(listIdPredictions);
        return true;

    }

}
