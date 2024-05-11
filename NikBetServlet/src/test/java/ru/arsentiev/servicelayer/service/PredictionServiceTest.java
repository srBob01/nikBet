package ru.arsentiev.servicelayer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.arsentiev.dto.prediction.controller.*;
import ru.arsentiev.entity.*;
import ru.arsentiev.exception.ServiceException;
import ru.arsentiev.mapper.PredictionMapper;
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

@ExtendWith(MockitoExtension.class)
class PredictionServiceTest {
    @Mock
    private PredictionMapper predictionMapper;
    @Mock
    private PredictionRepository predictionRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private PredictionService predictionService;

    private Prediction predictionNotPlayed;
    private Prediction predictionPlayed;
    private Game game;
    private User user;
    private BigDecimal summa;
    private float coefficient;

    @BeforeEach
    public void fillEntity() {
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
        when(predictionMapper.map(dto)).thenReturn(predictionNotPlayed);
        when(predictionRepository.insert(predictionNotPlayed)).thenReturn(false);

        assertThatThrownBy(() -> predictionService.insertPrediction(dto)).isInstanceOf(ServiceException.class);
    }

    @Test
    void insertPredictionTest_GameExistAndInsertGood() {
        PredictionPlaceControllerDto dto = new PredictionPlaceControllerDto(predictionNotPlayed.getUser().getIdUser(), predictionNotPlayed.getGame().getIdGame()
                , predictionNotPlayed.getSumma(), predictionNotPlayed.getPrediction());
        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.of(game));
        when(predictionMapper.map(dto)).thenReturn(predictionNotPlayed);
        when(predictionRepository.insert(predictionNotPlayed)).thenReturn(true);

        PredictionResultControllerDto resultControllerDto = predictionService.insertPrediction(dto);

        assertThat(resultControllerDto).isNotNull();
        assertThat(resultControllerDto.winner()).isEqualTo(game.getHomeTeam().getTitle());
        assertThat(resultControllerDto.possibleWin()).isEqualTo(summa.multiply(BigDecimal.valueOf(coefficient)));
        verify(predictionRepository, times(1)).insert(predictionNotPlayed);
    }

    @Test
    void selectDoublePredictionsListTest() {
        PredictionNotPlayedControllerDto predictionNotPlayedControllerDto = PredictionNotPlayedControllerDto.builder()
                .idPrediction(predictionNotPlayed.getIdPrediction()).build();
        PredictionPlayedControllerDto predictionPlayedControllerDto = PredictionPlayedControllerDto.builder()
                .idPrediction(predictionPlayed.getIdPrediction()).build();
        Long idUser = user.getIdUser();

        when(predictionRepository.selectByUserIdLimitBetPlayed(idUser)).thenReturn(Collections.singletonList(predictionPlayed));
        when(predictionRepository.selectByUserIdLimitBetNotPlayed(idUser)).thenReturn(Collections.singletonList(predictionNotPlayed));
        when(predictionMapper.mapPredictionPlayed(predictionPlayed)).thenReturn(predictionPlayedControllerDto);
        when(predictionMapper.mapPredictionNotPlayed(predictionNotPlayed)).thenReturn(predictionNotPlayedControllerDto);

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
        game.setStatus(GameStatus.Completed);

        when(gameRepository.selectById(dto.idGame())).thenReturn(Optional.of(game));

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
                .idPrediction(predictionNotPlayed.getIdPrediction()).build();
        Long idUser = user.getIdUser();

        when(predictionRepository.selectByUserIdBetNotPlayed(idUser)).thenReturn(Collections.singletonList(predictionNotPlayed));
        when(predictionMapper.mapPredictionNotPlayed(predictionNotPlayed)).thenReturn(predictionNotPlayedControllerDto);

        List<PredictionNotPlayedControllerDto> list = predictionService.selectBetNotPlayedPredictions(idUser);

        assertThat(list).isEqualTo(Collections.singletonList(predictionNotPlayedControllerDto));
    }

    @Test
    void selectBetPlayedPredictions() {
        PredictionPlayedControllerDto predictionPlayedControllerDto = PredictionPlayedControllerDto.builder()
                .idPrediction(predictionPlayed.getIdPrediction()).build();
        Long idUser = user.getIdUser();

        when(predictionRepository.selectByUserIdBetPlayed(idUser)).thenReturn(Collections.singletonList(predictionPlayed));
        when(predictionMapper.mapPredictionPlayed(predictionPlayed)).thenReturn(predictionPlayedControllerDto);

        List<PredictionPlayedControllerDto> list = predictionService.selectBetPlayedPredictions(idUser);

        assertThat(list).isEqualTo(Collections.singletonList(predictionPlayedControllerDto));
    }
}