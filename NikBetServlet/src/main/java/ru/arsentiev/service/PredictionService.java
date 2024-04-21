package ru.arsentiev.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.arsentiev.dto.prediction.controller.*;
import ru.arsentiev.dto.user.controller.UserMoneyControllerDto;
import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameStatus;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.manager.DaoManager;
import ru.arsentiev.mapper.PredictionMapper;
import ru.arsentiev.repository.GameDao;
import ru.arsentiev.repository.PredictionDao;
import ru.arsentiev.repository.UserDao;
import ru.arsentiev.service.entity.prediction.DoubleListPredictionControllerDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PredictionService {
    private static final PredictionService INSTANCE = new PredictionService();
    private final PredictionMapper predictionMapper = PredictionMapper.getInstance();
    private final PredictionDao predictionDao = DaoManager.getPredictionDao();
    private final GameDao gameDao = DaoManager.getGameDao();
    private final UserDao userDao = DaoManager.getUserDao();

    public static PredictionService getInstance() {
        return INSTANCE;
    }

    public PredictionResultControllerDto insertPrediction(PredictionPlaceControllerDto predictionPlaceControllerDto) {
        Optional<Game> game = gameDao.selectById(predictionPlaceControllerDto.idGame());
        if (game.isEmpty()) {
            throw new RuntimeException();
        }
        Float coefficient = switch (predictionPlaceControllerDto.prediction()) {
            case HomeWin -> game.get().getCoefficientOnHomeTeam();
            case Draw -> game.get().getCoefficientOnDraw();
            case AwayWin -> game.get().getCoefficientOnGuestTeam();
        };
        Prediction prediction = predictionMapper.map(predictionPlaceControllerDto);
        prediction.setCoefficient(coefficient);
        predictionDao.insert(prediction);
        String homeTeam = game.get().getHomeTeam().getTitle();
        String guestTeam = game.get().getGuestTeam().getTitle();
        String winner = switch (predictionPlaceControllerDto.prediction()) {
            case HomeWin -> homeTeam;
            case Draw -> "Draw";
            case AwayWin -> guestTeam;
        };
        BigDecimal possibleWin = predictionPlaceControllerDto.summa().multiply(BigDecimal.valueOf(coefficient));
        return PredictionResultControllerDto.builder()
                .winner(winner)
                .homeTeam(homeTeam)
                .guestTeam(guestTeam)
                .possibleWin(possibleWin)
                .build();
    }

    public DoubleListPredictionControllerDto selectDoublePredictionsList(Long idUser) {
        List<PredictionPlayedControllerDto> predictionBetPlayedControllerDtoList = predictionDao
                .selectByUserIdLimitBetPlayed(idUser)
                .stream()
                .map(predictionMapper::mapPredictionPlayed)
                .toList();
        List<PredictionNotPlayedControllerDto> predictionBetNotPlayedControllerDtoList = predictionDao
                .selectByUserIdLimitBetNotPlayed(idUser)
                .stream()
                .map(predictionMapper::mapPredictionNotPlayed)
                .toList();
        return DoubleListPredictionControllerDto.builder()
                .predictionControllerBetPlayedDtoList(predictionBetPlayedControllerDtoList)
                .predictionControllerBetNotPlayedDtoList(predictionBetNotPlayedControllerDtoList)
                .build();
    }

    public Optional<BigDecimal> deletePrediction(PredictionForDeleteControllerDto predictionForDeleteControllerDto) {
        Optional<Game> game = gameDao.selectById(predictionForDeleteControllerDto.idGame());
        if (game.isEmpty()) {
            throw new RuntimeException();
        }

        Float coefficientNow = switch (predictionForDeleteControllerDto.prediction()) {
            case HomeWin -> game.get().getCoefficientOnHomeTeam();
            case Draw -> game.get().getCoefficientOnDraw();
            case AwayWin -> game.get().getCoefficientOnGuestTeam();
        };

        if (game.get().getStatus().compareTo(GameStatus.Completed) == 0 ||
            coefficientNow - predictionForDeleteControllerDto.coefficient() > 5F) {
            return Optional.empty();
        }

        BigDecimal refund = predictionForDeleteControllerDto.summa()
                .divide(BigDecimal.valueOf(2L), 2, RoundingMode.HALF_UP);

        if (!predictionDao.delete(predictionForDeleteControllerDto.idPrediction())) {
            throw new RuntimeException();
        }

        UserMoneyControllerDto userMoneyControllerDto = UserMoneyControllerDto.builder()
                .idUser(predictionForDeleteControllerDto.idUser())
                .summa(refund)
                .build();
        userDao.depositMoneyById(userMoneyControllerDto);

        return Optional.of(refund);
    }

    public List<PredictionNotPlayedControllerDto> selectBetNotPlayedPredictions(Long idUser) {
        return predictionDao.selectByUserIdBetNotPlayed(idUser)
                .stream()
                .map(predictionMapper::mapPredictionNotPlayed)
                .toList();
    }

    public List<PredictionPlayedControllerDto> selectBetPlayedPredictions(Long idUser) {
        return predictionDao.selectByUserIdBetPlayed(idUser)
                .stream()
                .map(predictionMapper::mapPredictionPlayed)
                .toList();
    }
}
