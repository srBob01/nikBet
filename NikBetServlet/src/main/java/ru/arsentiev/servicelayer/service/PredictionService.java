package ru.arsentiev.servicelayer.service;

import ru.arsentiev.dto.prediction.controller.*;
import ru.arsentiev.dto.user.controller.UserMoneyControllerDto;
import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameStatus;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.PredictionMapper;
import ru.arsentiev.repository.GameRepository;
import ru.arsentiev.repository.PredictionRepository;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.servicelayer.service.entity.prediction.DoubleListPredictionControllerDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

public class PredictionService {
    private final PredictionMapper predictionMapper;
    private final PredictionRepository predictionRepository;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public PredictionService(PredictionMapper predictionMapper, PredictionRepository predictionRepository,
                             GameRepository gameRepository, UserRepository userRepository) {
        this.predictionMapper = predictionMapper;
        this.predictionRepository = predictionRepository;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public PredictionResultControllerDto insertPrediction(PredictionPlaceControllerDto predictionPlaceControllerDto) {
        Optional<Game> game = gameRepository.selectById(predictionPlaceControllerDto.idGame());
        if (game.isEmpty()) {
            throw new ServiceException("The game does not exist");
        }
        float coefficient = switch (predictionPlaceControllerDto.prediction()) {
            case HomeWin -> game.get().getCoefficientOnHomeTeam();
            case Draw -> game.get().getCoefficientOnDraw();
            case AwayWin -> game.get().getCoefficientOnGuestTeam();
        };
        Prediction prediction = predictionMapper.map(predictionPlaceControllerDto);
        prediction.setCoefficient(coefficient);
        if (!predictionRepository.insert(prediction)) {
            throw new ServiceException("The prediction does not insert");
        }
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
        List<PredictionPlayedControllerDto> predictionBetPlayedControllerDtoList = predictionRepository
                .selectByUserIdLimitBetPlayed(idUser)
                .stream()
                .map(predictionMapper::mapPredictionPlayed)
                .toList();
        List<PredictionNotPlayedControllerDto> predictionBetNotPlayedControllerDtoList = predictionRepository
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
        Optional<Game> game = gameRepository.selectById(predictionForDeleteControllerDto.idGame());
        if (game.isEmpty()) {
            throw new ServiceException("The game does not exist");
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

        if (!predictionRepository.delete(predictionForDeleteControllerDto.idPrediction())) {
            throw new ServiceException("The prediction does not delete");
        }

        UserMoneyControllerDto userMoneyControllerDto = UserMoneyControllerDto.builder()
                .idUser(predictionForDeleteControllerDto.idUser())
                .summa(refund)
                .build();
        userRepository.depositMoneyById(userMoneyControllerDto.idUser(), userMoneyControllerDto.summa());

        return Optional.of(refund);
    }

    public List<PredictionNotPlayedControllerDto> selectBetNotPlayedPredictions(Long idUser) {
        return predictionRepository.selectByUserIdBetNotPlayed(idUser)
                .stream()
                .map(predictionMapper::mapPredictionNotPlayed)
                .toList();
    }

    public List<PredictionPlayedControllerDto> selectBetPlayedPredictions(Long idUser) {
        return predictionRepository.selectByUserIdBetPlayed(idUser)
                .stream()
                .map(predictionMapper::mapPredictionPlayed)
                .toList();
    }
}
