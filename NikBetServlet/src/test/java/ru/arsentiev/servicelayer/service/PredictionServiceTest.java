package ru.arsentiev.servicelayer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.arsentiev.dto.prediction.controller.*;
import ru.arsentiev.entity.*;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.PredictionMapper;
import ru.arsentiev.processing.dateformatter.TimeStampFormatter;
import ru.arsentiev.repository.GameRepository;
import ru.arsentiev.repository.PredictionRepository;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.servicelayer.service.entity.prediction.DoubleListPredictionControllerDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class PredictionServiceTest {
    private final PredictionMapper predictionMapper;
    private PredictionRepository predictionRepository;
    private GameRepository gameRepository;
    private UserRepository userRepository;
    private PredictionService predictionService;

    private final Prediction predictionNotPlayed;
    private final Prediction predictionPlayed;
    private final Game game;
    private final User user;
    private final BigDecimal summa;
    private final float coefficient;

    {
        TimeStampFormatter timeStampFormatter = new TimeStampFormatter();
        predictionMapper = new PredictionMapper(timeStampFormatter);

        GameResult gameResult = GameResult.HomeWin;
        summa = new BigDecimal("100");
        coefficient = 1.5F;

        Team homeTeam = Team.builder().title("HomeTeam").build();
        Team guestTeam = Team.builder().title("GuestTeam").build();

        game = Game.builder()
                .idGame(1L)
                .homeTeam(homeTeam)
                .guestTeam(guestTeam)
                .coefficientOnHomeTeam(coefficient)
                .coefficientOnDraw(2.0f)
                .coefficientOnGuestTeam(2.5f)
                .goalHomeTeam(2)
                .goalGuestTeam(1)
                .status(GameStatus.Scheduled)
                .build();

        user = User.builder().idUser(1).build();

        predictionNotPlayed = Prediction.builder()
                .idPrediction(1)
                .game(game)
                .user(user)
                .predictionDate(LocalDateTime.now())
                .summa(summa)
                .prediction(gameResult)
                .coefficient(coefficient)
                .predictionStatus(PredictionStatus.BetNotPlayed)
                .build();

        predictionPlayed = Prediction.builder()
                .idPrediction(1)
                .game(game)
                .user(user)
                .predictionDate(LocalDateTime.now())
                .summa(summa)
                .prediction(gameResult)
                .coefficient(coefficient)
                .predictionStatus(PredictionStatus.BetPlayed)
                .build();
    }

    @BeforeEach
    void createPredictionService() {
        predictionRepository = mock(PredictionRepository.class);
        gameRepository = mock(GameRepository.class);
        userRepository = mock(UserRepository.class);
        predictionService = new PredictionService(predictionMapper, predictionRepository, gameRepository, userRepository);
    }

    @Test
    void insertPredictionTest_GameNotExist() {
        PredictionPlaceControllerDto dto = PredictionPlaceControllerDto.builder().idGame(game.getIdGame()).build();

        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> predictionService.insertPrediction(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void insertPredictionTest_GameExistAndInsertBad() {
        PredictionPlaceControllerDto dto = new PredictionPlaceControllerDto(predictionNotPlayed.getUser().getIdUser(), predictionNotPlayed.getGame().getIdGame()
                , predictionNotPlayed.getSumma(), predictionNotPlayed.getPrediction());
        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.of(game));
        when(predictionRepository.insert(predictionNotPlayed)).thenReturn(false);

        assertThatThrownBy(() -> predictionService.insertPrediction(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void insertPredictionTest_GameExistAndInsertGood() {
        PredictionPlaceControllerDto dto = new PredictionPlaceControllerDto(predictionNotPlayed.getUser().getIdUser(), predictionNotPlayed.getGame().getIdGame()
                , predictionNotPlayed.getSumma(), predictionNotPlayed.getPrediction());

        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.of(game));
        when(predictionRepository.insert(any(Prediction.class))).thenReturn(true);

        PredictionResultControllerDto resultControllerDto = predictionService.insertPrediction(dto);

        assertThat(resultControllerDto).isNotNull();
        assertThat(resultControllerDto.winner()).isEqualTo(game.getHomeTeam().getTitle());
        assertThat(resultControllerDto.possibleWin()).isEqualTo(summa.multiply(BigDecimal.valueOf(coefficient)));
    }

    @Test
    void selectDoublePredictionsListTest() {
        PredictionNotPlayedControllerDto predictionNotPlayedControllerDto = PredictionNotPlayedControllerDto.builder()
                .idPrediction(predictionNotPlayed.getIdPrediction())
                .goalHomeTeam(predictionNotPlayed.getGame().getGoalHomeTeam())
                .goalGuestTeam(predictionNotPlayed.getGame().getGoalGuestTeam())
                .summa(predictionNotPlayed.getSumma())
                .predictionDate(predictionNotPlayed.getPredictionDate())
                .prediction(predictionNotPlayed.getPrediction())
                .predictionStatus(predictionNotPlayed.getPredictionStatus())
                .idGame(predictionNotPlayed.getGame().getIdGame())
                .coefficient(coefficient)
                .summa(summa)
                .homeTeam(predictionNotPlayed.getGame().getHomeTeam().getTitle())
                .guestTeam(predictionNotPlayed.getGame().getGuestTeam().getTitle())
                .build();

        PredictionPlayedControllerDto predictionPlayedControllerDto = PredictionPlayedControllerDto.builder()
                .idPrediction(predictionPlayed.getIdPrediction())
                .goalHomeTeam(predictionPlayed.getGame().getGoalHomeTeam())
                .goalGuestTeam(predictionPlayed.getGame().getGoalGuestTeam())
                .summa(predictionPlayed.getSumma())
                .predictionDate(predictionPlayed.getPredictionDate())
                .prediction(predictionPlayed.getPrediction())
                .predictionStatus(predictionPlayed.getPredictionStatus())
                .idGame(predictionPlayed.getGame().getIdGame())
                .coefficient(coefficient)
                .summa(summa)
                .homeTeam(predictionPlayed.getGame().getHomeTeam().getTitle())
                .guestTeam(predictionPlayed.getGame().getGuestTeam().getTitle())
                .build();

        Long idUser = user.getIdUser();

        when(predictionRepository.selectByUserIdLimitBetPlayed(idUser)).thenReturn(Collections.singletonList(predictionPlayed));
        when(predictionRepository.selectByUserIdLimitBetNotPlayed(idUser)).thenReturn(Collections.singletonList(predictionNotPlayed));

        DoubleListPredictionControllerDto result = predictionService.selectDoublePredictionsList(idUser);

        assertThat(result.predictionControllerBetNotPlayedDtoList()).isEqualTo(Collections.singletonList(predictionNotPlayedControllerDto));
        assertThat(result.predictionControllerBetPlayedDtoList()).isEqualTo(Collections.singletonList(predictionPlayedControllerDto));
    }

    @Test
    void deletePredictionTest_GameNotExists() {
        PredictionForDeleteControllerDto dto = PredictionForDeleteControllerDto.builder().idGame(game.getIdGame()).build();

        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> predictionService.deletePrediction(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void deletePredictionTest_StatusCompleted() {
        PredictionForDeleteControllerDto dto = PredictionForDeleteControllerDto.builder()
                .idGame(game.getIdGame()).prediction(GameResult.HomeWin).coefficient(5F).build();

        Game gameForDelete = game;
        gameForDelete.setStatus(GameStatus.Completed);

        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.of(gameForDelete));

        assertThat(predictionService.deletePrediction(dto)).isEmpty();
    }

    @Test
    void deletePredictionTest_BadDelete() {
        PredictionForDeleteControllerDto dto = PredictionForDeleteControllerDto.builder()
                .idGame(game.getIdGame()).prediction(GameResult.HomeWin).coefficient(5F).summa(summa).build();

        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.of(game));
        when(predictionRepository.delete(dto.idPrediction())).thenReturn(false);

        assertThatThrownBy(() -> predictionService.deletePrediction(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void deletePredictionTest_GoodDelete() {
        long idUser = predictionNotPlayed.getUser().getIdUser();
        PredictionForDeleteControllerDto dto = PredictionForDeleteControllerDto.builder()
                .idGame(game.getIdGame()).idUser(user.getIdUser()).prediction(GameResult.HomeWin).coefficient(5F).summa(summa).build();

        BigDecimal refund = summa.divide(BigDecimal.valueOf(2L), 2, RoundingMode.HALF_UP);

        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.of(game));
        when(predictionRepository.delete(dto.idPrediction())).thenReturn(true);
        when(userRepository.depositMoneyById(anyLong(), any())).thenReturn(true);

        Optional<BigDecimal> refundFromDelete = predictionService.deletePrediction(dto);

        assertThat(refundFromDelete).isPresent();
        assertThat(refundFromDelete.get()).isEqualTo(refund);
        verify(userRepository, times(1)).depositMoneyById(idUser, refund);
    }

    @Test
    void selectBetNotPlayedPredictions() {
        PredictionNotPlayedControllerDto predictionNotPlayedControllerDto = PredictionNotPlayedControllerDto.builder()
                .idPrediction(predictionNotPlayed.getIdPrediction())
                .goalHomeTeam(predictionNotPlayed.getGame().getGoalHomeTeam())
                .goalGuestTeam(predictionNotPlayed.getGame().getGoalGuestTeam())
                .summa(predictionNotPlayed.getSumma())
                .predictionDate(predictionNotPlayed.getPredictionDate())
                .prediction(predictionNotPlayed.getPrediction())
                .predictionStatus(predictionNotPlayed.getPredictionStatus())
                .idGame(predictionNotPlayed.getGame().getIdGame())
                .coefficient(coefficient)
                .summa(summa)
                .homeTeam(predictionNotPlayed.getGame().getHomeTeam().getTitle())
                .guestTeam(predictionNotPlayed.getGame().getGuestTeam().getTitle())
                .build();

        Long idUser = user.getIdUser();

        when(predictionRepository.selectByUserIdBetNotPlayed(idUser)).thenReturn(Collections.singletonList(predictionNotPlayed));

        List<PredictionNotPlayedControllerDto> list = predictionService.selectBetNotPlayedPredictions(idUser);

        assertThat(list).isEqualTo(Collections.singletonList(predictionNotPlayedControllerDto));
    }

    @Test
    void selectBetPlayedPredictions() {
        PredictionPlayedControllerDto predictionPlayedControllerDto = PredictionPlayedControllerDto.builder()
                .idPrediction(predictionPlayed.getIdPrediction())
                .goalHomeTeam(predictionPlayed.getGame().getGoalHomeTeam())
                .goalGuestTeam(predictionPlayed.getGame().getGoalGuestTeam())
                .summa(predictionPlayed.getSumma())
                .predictionDate(predictionPlayed.getPredictionDate())
                .prediction(predictionPlayed.getPrediction())
                .predictionStatus(predictionPlayed.getPredictionStatus())
                .idGame(predictionPlayed.getGame().getIdGame())
                .coefficient(coefficient)
                .summa(summa)
                .homeTeam(predictionPlayed.getGame().getHomeTeam().getTitle())
                .guestTeam(predictionPlayed.getGame().getGuestTeam().getTitle())
                .build();

        Long idUser = user.getIdUser();

        when(predictionRepository.selectByUserIdBetPlayed(idUser)).thenReturn(Collections.singletonList(predictionPlayed));

        List<PredictionPlayedControllerDto> list = predictionService.selectBetPlayedPredictions(idUser);

        assertThat(list).isEqualTo(Collections.singletonList(predictionPlayedControllerDto));
    }
}