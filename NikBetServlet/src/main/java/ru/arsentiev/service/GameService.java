package ru.arsentiev.service;

import ru.arsentiev.dto.game.controller.*;
import ru.arsentiev.dto.user.controller.UserMoneyControllerDto;
import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameStatus;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.processing.query.entity.CompletedGameFields;
import ru.arsentiev.repository.GameRepository;
import ru.arsentiev.repository.PredictionRepository;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.service.entity.game.TripleListOfGameControllerDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class GameService {
    private final GameRepository gameRepository;
    private final PredictionRepository predictionRepository;
    private final UserRepository userRepository;
    private final GameMapper gameMapper;

    public GameService(GameRepository gameRepository, PredictionRepository predictionRepository, UserRepository userRepository, GameMapper gameMapper) {
        this.gameRepository = gameRepository;
        this.predictionRepository = predictionRepository;
        this.userRepository = userRepository;
        this.gameMapper = gameMapper;
    }

    public List<GameProgressControllerDto> selectGameInProgressLimit() {
        return gameRepository.selectLimitGameInProgress().stream()
                .map(gameMapper::mapGameToControllerInProgress)
                .collect(toList());
    }

    public List<GameScheduledControllerDto> selectGameScheduledLimit() {
        return gameRepository.selectLimitGameScheduled().stream()
                .map(gameMapper::mapGameToControllerScheduled)
                .collect(toList());
    }

    public List<GameCompletedControllerDto> selectGameCompletedLimit() {
        return gameRepository.selectLimitGameCompleted().stream()
                .map(gameMapper::mapGameToControllerCompleted)
                .collect(toList());
    }

    public List<GameProgressControllerDto> selectGameInProgressAll() {
        return gameRepository.selectAllGameInProgress().stream()
                .map(gameMapper::mapGameToControllerInProgress)
                .collect(toList());
    }

    public List<GameScheduledControllerDto> selectGameScheduledAll() {
        return gameRepository.selectAllGameScheduled().stream()
                .map(gameMapper::mapGameToControllerScheduled)
                .collect(toList());
    }

    public List<GameCompletedControllerDto> selectGameCompletedAll() {
        return gameRepository.selectAllGameCompleted().stream()
                .map(gameMapper::mapGameToControllerCompleted)
                .collect(toList());
    }

    public List<GameScheduledControllerDto> selectHotGame() {
        return gameRepository.selectHotGameScheduled().stream()
                .map(gameMapper::mapGameToControllerScheduled)
                .toList();
    }

    public TripleListOfGameControllerDto selectGameByParameters(CompletedGameFields completedGameFields,
                                                                GameParametersControllerDto gameParametersControllerDto) {
        Game game = gameMapper.map(gameParametersControllerDto);
        List<Game> gameList = gameRepository.selectByParameters(game, completedGameFields);
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
        gameRepository.insert(game);
        return true;
    }

    public boolean startGame(Long idGame) {
        return gameRepository.startGame(idGame);
    }

    public boolean updateDescriptionGame(GameAdminProgressControllerDto gameAdminProgressControllerDto) {
        Game game = gameMapper.mapAdminProgressControllerToGame(gameAdminProgressControllerDto);
        return gameRepository.updateDescriptionGame(game);
    }

    public boolean startSecondHalf(Long idGame) {
        return gameRepository.startSecondHalf(idGame);
    }

    public boolean endGame(GameAdminCompletedControllerDto gameAdminCompletedControllerDto) {
        Game game = gameMapper.mapAdminCompletedControllerToGame(gameAdminCompletedControllerDto);
        if (!gameRepository.endGame(game)) {
            return false;
        }
        List<Prediction> predictions = predictionRepository.selectByGameId(gameAdminCompletedControllerDto.idGame());
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
                    userRepository.depositMoneyById(userMoneyControllerDto);
                });
        List<Long> listIdPredictions = predictions.stream()
                .map(Prediction::getIdPrediction)
                .toList();
        predictionRepository.updatePredictionStatusOfList(listIdPredictions);
        return true;
    }
}
