package ru.arsentiev.servicelayer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.arsentiev.dto.game.controller.*;
import ru.arsentiev.entity.*;
import ru.arsentiev.mapper.GameMapper;
import ru.arsentiev.processing.dateformatter.TimeStampFormatter;
import ru.arsentiev.processing.query.entity.CompletedGameFields;
import ru.arsentiev.repository.GameRepository;
import ru.arsentiev.repository.PredictionRepository;
import ru.arsentiev.repository.UserRepository;
import ru.arsentiev.servicelayer.service.entity.game.TripleListOfGameControllerDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GameServiceTest {
    private GameRepository gameRepository;
    private PredictionRepository predictionRepository;
    private UserRepository userRepository;
    private final GameMapper gameMapper;
    private GameService gameService;
    private final Game gameScheduled;
    private final Game gameInProgress;
    private final Game gameCompleted;
    private final GameScheduledControllerDto gameScheduledControllerDto;
    private final GameProgressControllerDto gameProgressControllerDto;
    private final GameCompletedControllerDto gameCompletedControllerDto;
    private final Game game;

    {
        TimeStampFormatter timeStampFormatter = new TimeStampFormatter();
        gameMapper = new GameMapper(timeStampFormatter);
        gameScheduled = Game.builder()
                .idGame(1)
                .homeTeam(Team.builder().title("Arsenal").build())
                .guestTeam(Team.builder().title("Chelsea").build())
                .status(GameStatus.Scheduled)
                .build();
        gameInProgress = Game.builder()
                .idGame(2)
                .homeTeam(Team.builder().title("Arsenal").build())
                .guestTeam(Team.builder().title("Chelsea").build())
                .goalGuestTeam(0)
                .goalHomeTeam(0)
                .status(GameStatus.InProgress)
                .build();
        gameCompleted = Game.builder()
                .idGame(3)
                .homeTeam(Team.builder().title("Arsenal").build())
                .guestTeam(Team.builder().title("Chelsea").build())
                .goalGuestTeam(0)
                .goalHomeTeam(0)
                .status(GameStatus.Completed)
                .result(GameResult.Draw)
                .build();

        gameScheduledControllerDto = GameScheduledControllerDto.builder()
                .idGame(gameScheduled.getIdGame())
                .homeTeam("Arsenal")
                .guestTeam("Chelsea")
                .build();
        gameProgressControllerDto = GameProgressControllerDto.builder()
                .idGame(gameInProgress.getIdGame())
                .homeTeam("Arsenal")
                .guestTeam("Chelsea")
                .goalGuestTeam(0)
                .goalHomeTeam(0)
                .build();
        gameCompletedControllerDto = GameCompletedControllerDto.builder()
                .idGame(gameCompleted.getIdGame())
                .homeTeam("Arsenal")
                .guestTeam("Chelsea")
                .goalGuestTeam(0)
                .goalHomeTeam(0)
                .result(GameResult.Draw)
                .build();

        game = Game.builder().idGame(1)
                .homeTeam(Team.builder().idTeam(1).build())
                .guestTeam(Team.builder().idTeam(2).build())
                .status(GameStatus.Scheduled)
                .gameDate(LocalDateTime.now().plusMinutes(3))
                .coefficientOnHomeTeam(1)
                .coefficientOnGuestTeam(1.4F)
                .coefficientOnDraw(3)
                .result(GameResult.HomeWin).build();
    }

    @BeforeEach
    void createGameService() {
        gameRepository = mock(GameRepository.class);
        predictionRepository = mock(PredictionRepository.class);
        userRepository = mock(UserRepository.class);
        gameService = new GameService(gameRepository, predictionRepository, userRepository, gameMapper);
    }

    @Test
    void selectGameScheduledTest() {

        when(gameRepository.selectAllGameScheduled()).thenReturn(Collections.singletonList(gameScheduled));
        when(gameRepository.selectLimitGameScheduled()).thenReturn(Collections.singletonList(gameScheduled));

        List<GameScheduledControllerDto> scheduledControllerDtos = gameService.selectGameScheduledAll();
        List<GameScheduledControllerDto> scheduledControllerDtosLimit = gameService.selectGameScheduledLimit();

        assertThat(scheduledControllerDtos).isNotNull();
        assertThat(scheduledControllerDtos).isNotEmpty();
        assertThat(scheduledControllerDtos).contains(gameScheduledControllerDto);

        assertThat(scheduledControllerDtosLimit).isNotNull();
        assertThat(scheduledControllerDtosLimit).isNotEmpty();
        assertThat(scheduledControllerDtosLimit).contains(gameScheduledControllerDto);
    }

    @Test
    void selectGameInProgressTest() {

        when(gameRepository.selectAllGameInProgress()).thenReturn(Collections.singletonList(gameInProgress));
        when(gameRepository.selectLimitGameInProgress()).thenReturn(Collections.singletonList(gameInProgress));

        List<GameProgressControllerDto> progressControllerDtos = gameService.selectGameInProgressAll();
        List<GameProgressControllerDto> progressControllerDtosLimit = gameService.selectGameInProgressLimit();

        assertThat(progressControllerDtos).isNotNull();
        assertThat(progressControllerDtos).isNotEmpty();
        assertThat(progressControllerDtos).contains(gameProgressControllerDto);

        assertThat(progressControllerDtosLimit).isNotNull();
        assertThat(progressControllerDtosLimit).isNotEmpty();
        assertThat(progressControllerDtosLimit).contains(gameProgressControllerDto);
    }

    @Test
    void selectGameCompletedTest() {

        when(gameRepository.selectAllGameCompleted()).thenReturn(Collections.singletonList(gameCompleted));
        when(gameRepository.selectLimitGameCompleted()).thenReturn(Collections.singletonList(gameCompleted));

        List<GameCompletedControllerDto> completedControllerDtos = gameService.selectGameCompletedAll();
        List<GameCompletedControllerDto> completedControllerDtosLimit = gameService.selectGameCompletedLimit();

        assertThat(completedControllerDtos).isNotNull();
        assertThat(completedControllerDtos).isNotEmpty();
        assertThat(completedControllerDtos).contains(gameCompletedControllerDto);

        assertThat(completedControllerDtosLimit).isNotNull();
        assertThat(completedControllerDtosLimit).isNotEmpty();
        assertThat(completedControllerDtosLimit).contains(gameCompletedControllerDto);
    }

    @Test
    void selectGameScheduledEmptyTest() {
        when(gameRepository.selectAllGameScheduled()).thenReturn(Collections.emptyList());
        when(gameRepository.selectLimitGameScheduled()).thenReturn(Collections.emptyList());

        List<GameScheduledControllerDto> scheduledControllerDtos = gameService.selectGameScheduledAll();
        List<GameScheduledControllerDto> scheduledControllerDtosLimit = gameService.selectGameScheduledLimit();

        assertThat(scheduledControllerDtos).isNotNull();
        assertThat(scheduledControllerDtos).isEmpty();

        assertThat(scheduledControllerDtosLimit).isNotNull();
        assertThat(scheduledControllerDtosLimit).isEmpty();
    }

    @Test
    void selectGameInProgressEmptyTest() {

        when(gameRepository.selectAllGameInProgress()).thenReturn(Collections.emptyList());
        when(gameRepository.selectLimitGameInProgress()).thenReturn(Collections.emptyList());

        List<GameProgressControllerDto> progressControllerDtos = gameService.selectGameInProgressAll();
        List<GameProgressControllerDto> progressControllerDtosLimit = gameService.selectGameInProgressLimit();

        assertThat(progressControllerDtos).isNotNull();
        assertThat(progressControllerDtos).isEmpty();

        assertThat(progressControllerDtosLimit).isNotNull();
        assertThat(progressControllerDtosLimit).isEmpty();
    }

    @Test
    void selectGameCompletedEmptyTest() {

        when(gameRepository.selectAllGameCompleted()).thenReturn(Collections.emptyList());
        when(gameRepository.selectLimitGameCompleted()).thenReturn(Collections.emptyList());

        List<GameCompletedControllerDto> completedControllerDtos = gameService.selectGameCompletedAll();
        List<GameCompletedControllerDto> completedControllerDtosLimit = gameService.selectGameCompletedLimit();

        assertThat(completedControllerDtos).isNotNull();
        assertThat(completedControllerDtos).isEmpty();

        assertThat(completedControllerDtosLimit).isNotNull();
        assertThat(completedControllerDtosLimit).isEmpty();
    }

    @Test
    void selectGameHotTest() {

        when(gameRepository.selectHotGameScheduled()).thenReturn(Collections.singletonList(gameScheduled));

        List<GameScheduledControllerDto> scheduledControllerDtos = gameService.selectHotGame();

        assertThat(scheduledControllerDtos).isNotNull();
        assertThat(scheduledControllerDtos).isNotEmpty();
        assertThat(scheduledControllerDtos).contains(gameScheduledControllerDto);
    }

    @Test
    void selectGameHotEmptyTest() {

        when(gameRepository.selectHotGameScheduled()).thenReturn(Collections.emptyList());

        List<GameScheduledControllerDto> scheduledControllerDtos = gameService.selectHotGame();

        assertThat(scheduledControllerDtos).isNotNull();
        assertThat(scheduledControllerDtos).isEmpty();
    }

    @Test
    void selectGameByParametersTest() {
        Game gameForParameters = Game.builder()
                .homeTeam(game.getHomeTeam())
                .guestTeam(game.getGuestTeam())
                .result(game.getResult())
                .status(game.getStatus())
                .build();
        List<Game> gameList = List.of(gameScheduled, gameInProgress, gameCompleted);

        CompletedGameFields completedGameFields = CompletedGameFields.builder().isCompletedStatusGame(true).build();
        GameParametersControllerDto gameParametersControllerDto = GameParametersControllerDto.builder()
                .homeTeamId(game.getHomeTeam().getIdTeam())
                .guestTeamId(game.getGuestTeam().getIdTeam())
                .result(game.getResult())
                .status(game.getStatus()).build();

        when(gameRepository.selectByParameters(gameForParameters, completedGameFields)).thenReturn(gameList);

        TripleListOfGameControllerDto result = gameService.selectGameByParameters(completedGameFields, gameParametersControllerDto);

        assertThat(result).isNotNull();
        assertThat(result.gameViewScheduledDtoList()).isEqualTo(Collections.singletonList(gameScheduledControllerDto));
        assertThat(result.gameViewInProgressDtoList()).isEqualTo(Collections.singletonList(gameProgressControllerDto));
        assertThat(result.gameViewCompletedDtoList()).isEqualTo(Collections.singletonList(gameCompletedControllerDto));
    }

    @Test
    void addNewGameEqualIdHomeAndTeamTest() {
        GameAdminScheduledControllerDto gameAdminScheduledControllerDto = GameAdminScheduledControllerDto.builder().idHomeTeam(1).idGuestTeam(1).build();

        assertThat(gameService.addNewGame(gameAdminScheduledControllerDto)).isFalse();
        verify(gameRepository, never()).insert(any());
    }

    @Test
    void addNewGameWrongTimeTest() {
        GameAdminScheduledControllerDto gameAdminScheduledControllerDto = GameAdminScheduledControllerDto.builder()
                .idHomeTeam(1).idGuestTeam(2).gameDate(LocalDateTime.now().minusMinutes(1)).build();

        assertThat(gameService.addNewGame(gameAdminScheduledControllerDto)).isFalse();
        verify(gameRepository, never()).insert(any());
    }

    @Test
    void addNewGameValidTest() {
        GameAdminScheduledControllerDto gameAdminScheduledControllerDto = GameAdminScheduledControllerDto.builder()
                .idHomeTeam(game.getHomeTeam().getIdTeam())
                .idGuestTeam(game.getGuestTeam().getIdTeam())
                .gameDate(game.getGameDate())
                .coefficientOnDraw(game.getCoefficientOnDraw())
                .coefficientOnHomeTeam(game.getCoefficientOnHomeTeam())
                .coefficientOnGuestTeam(game.getCoefficientOnGuestTeam())
                .build();

        assertThat(gameService.addNewGame(gameAdminScheduledControllerDto)).isTrue();
    }

    @Test
    void startGameTrueTest() {
        long idGame = 1;
        when(gameRepository.startGame(idGame)).thenReturn(true);

        assertThat(gameService.startGame(idGame)).isTrue();
    }

    @Test
    void startGameFalseTest() {
        long idGame = 1;
        when(gameRepository.startGame(idGame)).thenReturn(false);

        assertThat(gameService.startGame(idGame)).isFalse();
    }

    @Test
    void startSecondHalfTrueTest() {
        long idGame = 1;
        when(gameRepository.startSecondHalf(idGame)).thenReturn(true);

        assertThat(gameService.startSecondHalf(idGame)).isTrue();
    }

    @Test
    void startSecondHalfFalseTest() {
        long idGame = 1;
        when(gameRepository.startSecondHalf(idGame)).thenReturn(false);

        assertThat(gameService.startSecondHalf(idGame)).isFalse();
    }

    @Test
    void updateDescriptionGameFalseTest() {
        GameAdminProgressControllerDto gameAdminProgressControllerDto = GameAdminProgressControllerDto.builder().idGame(game.getIdGame()).build();
        when(gameRepository.updateDescriptionGame(game)).thenReturn(false);

        assertThat(gameService.updateDescriptionGame(gameAdminProgressControllerDto)).isFalse();
    }

    @Test
    void updateDescriptionGameTrueTest() {
        Game gameForUpdate = Game.builder()
                .goalHomeTeam(gameInProgress.getGoalHomeTeam())
                .goalGuestTeam(gameInProgress.getGoalGuestTeam())
                .coefficientOnHomeTeam(game.getCoefficientOnHomeTeam())
                .coefficientOnDraw(game.getCoefficientOnDraw())
                .coefficientOnGuestTeam(game.getCoefficientOnGuestTeam())
                .idGame(game.getIdGame())
                .build();

        GameAdminProgressControllerDto gameAdminProgressControllerDto = GameAdminProgressControllerDto.builder()
                .goalHomeTeam(gameInProgress.getGoalHomeTeam())
                .goalGuestTeam(gameInProgress.getGoalGuestTeam())
                .coefficientOnHomeTeam(game.getCoefficientOnHomeTeam())
                .coefficientOnDraw(game.getCoefficientOnDraw())
                .coefficientOnGuestTeam(game.getCoefficientOnGuestTeam())
                .idGame(game.getIdGame()).build();
        when(gameRepository.updateDescriptionGame(gameForUpdate)).thenReturn(true);

        assertThat(gameService.updateDescriptionGame(gameAdminProgressControllerDto)).isTrue();
    }

    @Test
    void endGameInvalid() {
        GameAdminCompletedControllerDto gameAdminCompletedControllerDto = GameAdminCompletedControllerDto.builder().build();

        when(gameRepository.endGame(game.getIdGame(), game.getResult())).thenReturn(false);

        assertThat(gameService.endGame(gameAdminCompletedControllerDto)).isFalse();
        verify(userRepository, never()).depositMoneyById(anyLong(), any());
    }

    @Test
    void endGameValidWithWin() {
        GameAdminCompletedControllerDto gameAdminCompletedControllerDto = GameAdminCompletedControllerDto.builder()
                .idGame(game.getIdGame())
                .result(game.getResult())
                .build();
        User user = User.builder().idUser(1).build();
        Prediction prediction = Prediction.builder()
                .idPrediction(1)
                .game(game)
                .user(user)
                .predictionDate(LocalDateTime.now())
                .summa(new BigDecimal("100"))
                .prediction(game.getResult())
                .coefficient(2.0f)
                .build();

        when(gameRepository.endGame(game.getIdGame(), game.getResult())).thenReturn(true);
        when(predictionRepository.selectByGameId(gameAdminCompletedControllerDto.idGame())).thenReturn(Collections.singletonList(prediction));
        when(userRepository.depositMoneyById(anyLong(), any())).thenReturn(true);

        boolean result = gameService.endGame(gameAdminCompletedControllerDto);

        assertThat(result).isTrue();
        verify(predictionRepository, times(1)).updatePredictionStatusOfList(Collections.singletonList(prediction.getIdPrediction()));
        verify(userRepository, times(1)).depositMoneyById(eq(user.getIdUser()), any());
    }

    @Test
    void endGameValidWithLose() {
        GameAdminCompletedControllerDto gameAdminCompletedControllerDto = GameAdminCompletedControllerDto.builder()
                .idGame(game.getIdGame())
                .result(game.getResult())
                .build();
        User user = User.builder().idUser(1).build();
        Prediction prediction = Prediction.builder()
                .idPrediction(1)
                .game(game)
                .user(user)
                .predictionDate(LocalDateTime.now())
                .summa(new BigDecimal("100"))
                .prediction(GameResult.Draw)
                .coefficient(2.0f)
                .build();

        when(gameRepository.endGame(game.getIdGame(), game.getResult())).thenReturn(true);
        when(predictionRepository.selectByGameId(gameAdminCompletedControllerDto.idGame())).thenReturn(Collections.singletonList(prediction));

        boolean result = gameService.endGame(gameAdminCompletedControllerDto);

        assertThat(result).isTrue();
        verify(predictionRepository, times(1)).updatePredictionStatusOfList(Collections.singletonList(prediction.getIdPrediction()));
        verify(userRepository, never()).depositMoneyById(eq(user.getIdUser()), any());
    }
}