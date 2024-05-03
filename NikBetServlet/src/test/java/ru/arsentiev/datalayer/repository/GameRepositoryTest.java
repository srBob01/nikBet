package ru.arsentiev.datalayer.repository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.arsentiev.datalayer.TestConnectionGetter;
import ru.arsentiev.entity.*;
import ru.arsentiev.exception.RepositoryException;
import ru.arsentiev.processing.query.GameQueryCreator;
import ru.arsentiev.processing.query.entity.CompletedGameFields;
import ru.arsentiev.repository.GameRepository;
import ru.arsentiev.repository.TeamRepository;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GameRepositoryTest {
    private final Team team1;
    private final Team team2;
    private final TestConnectionGetter connectionGetter = TestConnectionGetter.getInstance();
    private final TeamRepository teamRepository = new TeamRepository(connectionGetter);
    private final GameQueryCreator gameQueryCreator = new GameQueryCreator();
    private final GameRepository gameRepository = new GameRepository(connectionGetter, teamRepository, gameQueryCreator);
    private final CompletedGameFields allCompletedGameFields;
    private final CompletedGameFields nothingCompletedGameFields;
    private final CompletedGameFields statusCompletedGameFields;

    {
        allCompletedGameFields = CompletedGameFields.builder()
                .isCompletedResultGame(true)
                .isCompletedStatusGame(true)
                .isCompletedGuestTeam(true)
                .isCompletedHomeTeam(true)
                .build();

        nothingCompletedGameFields = CompletedGameFields.builder()
                .isCompletedResultGame(false)
                .isCompletedStatusGame(false)
                .isCompletedGuestTeam(false)
                .isCompletedHomeTeam(false)
                .build();

        statusCompletedGameFields = CompletedGameFields.builder()
                .isCompletedResultGame(false)
                .isCompletedStatusGame(true)
                .isCompletedGuestTeam(false)
                .isCompletedHomeTeam(false)
                .build();

        team1 = Team.builder()
                .title("Liverpool")
                .abbreviation("LIV")
                .build();

        team2 = Team.builder()
                .title("Chelsea")
                .abbreviation("CHE")
                .build();
    }

    private Stream<Game> generateInValidGame() {
        Game game1 = Game.builder()
                .homeTeam(team2)
                .gameDate(LocalDateTime.of(LocalDate.of(2024, 4, 22), LocalTime.of(15, 0, 0)))
                .coefficientOnHomeTeam(3.5F)
                .coefficientOnGuestTeam(4.4F)
                .build();

        Game game2 = Game.builder()
                .guestTeam(team2)
                .homeTeam(team1)
                .coefficientOnDraw(2.3F)
                .coefficientOnHomeTeam(3.5F)
                .coefficientOnGuestTeam(4.4F)
                .build();

        return Stream.of(game1, game2);
    }

    private Game defaultScheduledGame() {
        return Game.builder()
                .guestTeam(team2)
                .homeTeam(team1)
                .gameDate(LocalDateTime.of(LocalDate.of(2024, 4, 22), LocalTime.of(15, 0, 0)))
                .status(GameStatus.Scheduled)
                .coefficientOnDraw(2.3F)
                .coefficientOnHomeTeam(3.5F)
                .coefficientOnGuestTeam(4.4F)
                .build();
    }

    private List<Game> generateGames(int size) {
        List<Game> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(defaultScheduledGame());
        }
        return result;
    }

    @SneakyThrows
    @BeforeAll
    public void insertTeams() {
        try (Connection connection = connectionGetter.get()) {
            //language=PostgreSQL
            String CLEAR_TABLE = "TRUNCATE nikbet_test.public.teams RESTART IDENTITY CASCADE";
            connection.prepareStatement(CLEAR_TABLE).executeUpdate();
        }
        teamRepository.insert(team1);
        teamRepository.insert(team2);
    }

    @SneakyThrows
    @BeforeEach
    public void clear() {
        try (Connection connection = connectionGetter.get()) {
            //language=PostgreSQL
            String CLEAR_TABLE = "TRUNCATE nikbet_test.public.games RESTART IDENTITY CASCADE";
            connection.prepareStatement(CLEAR_TABLE).executeUpdate();
        }
    }

    @Test
    void insertValidGameTest() {
        Game game = defaultScheduledGame();

        assertThat(gameRepository.insert(game)).isTrue();
        assertThat(game.getIdGame()).isNotZero();

        Optional<Game> gameOptional = gameRepository.selectById(game.getIdGame());
        assertThat(gameOptional.isPresent()).isTrue();
        Game gameInsert = gameOptional.get();
        assertThat(gameInsert).isEqualTo(game);
    }

    @ParameterizedTest
    @MethodSource("generateInValidGame")
    void insertInvalidUserTest(Game game) {
        assertThatThrownBy(() -> gameRepository.insert(game)).isInstanceOf(RepositoryException.class);
    }

    @Test
    void selectByValidIdTest() {
        Game game = defaultScheduledGame();
        gameRepository.insert(game);

        Optional<Game> gameFromDBOptional = gameRepository.selectById(game.getIdGame());
        assertThat(gameFromDBOptional.isPresent()).isTrue();

        Game gameFromDB = gameFromDBOptional.get();
        assertThat(gameFromDB).isEqualTo(game);
    }

    @Test
    void selectByInvalidIdTest() {
        long wrongId = -1;
        assertThat(gameRepository.selectById(wrongId)).isEmpty();
    }

    @Test
    void selectByNullIdTest() {
        assertThatThrownBy(() -> gameRepository.selectById(null)).isInstanceOf(RepositoryException.class);
    }

    @Test
    void functionWithValidGameTest() {
        Game game = defaultScheduledGame();
        gameRepository.insert(game);

        game.setStatus(GameStatus.InProgress);
        game.setGoalGuestTeam(0);
        game.setGoalHomeTeam(0);
        game.setTime(GameTime.Time1);
        assertThat(gameRepository.startGame(game.getIdGame())).isTrue();

        Optional<Game> gameFromDBStartOptional = gameRepository.selectById(game.getIdGame());
        assertThat(gameFromDBStartOptional.isPresent()).isTrue();

        Game gameFromDBStart = gameFromDBStartOptional.get();
        assertThat(gameFromDBStart).isEqualTo(game);

        game.setTime(GameTime.Time2);
        assertThat(gameRepository.startSecondHalf(game.getIdGame())).isTrue();

        Optional<Game> gameFromDBStarSecondTimeOptional = gameRepository.selectById(game.getIdGame());
        assertThat(gameFromDBStarSecondTimeOptional.isPresent()).isTrue();

        Game gameFromDBStarSecondTime = gameFromDBStarSecondTimeOptional.get();
        assertThat(gameFromDBStarSecondTime).isEqualTo(game);

        game.setStatus(GameStatus.Completed);
        game.setResult(GameResult.Draw);
        assertThat(gameRepository.endGame(game.getIdGame(), game.getResult())).isTrue();

        Optional<Game> gameFromDBEndOptional = gameRepository.selectById(game.getIdGame());
        assertThat(gameFromDBEndOptional.isPresent()).isTrue();

        Game gameFromDBEnd = gameFromDBEndOptional.get();
        assertThat(gameFromDBEnd).isEqualTo(game);
    }

    @Test
    void functionWithInvalidGameTest() {
        long wrongId = -1;

        assertThat(gameRepository.startGame(wrongId)).isFalse();

        assertThat(gameRepository.startSecondHalf(wrongId)).isFalse();

        assertThat(gameRepository.endGame(wrongId, GameResult.Draw)).isFalse();
    }

    @Test
    void functionWithNullGameTest() {
        assertThatThrownBy(() -> assertThat(gameRepository.startGame(null))).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> assertThat(assertThat(gameRepository.startSecondHalf(null)))).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> assertThat(assertThat(gameRepository.endGame(null, null)))).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> assertThat(assertThat(gameRepository.endGame(1L, null)))).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> assertThat(assertThat(gameRepository.endGame(null, GameResult.Draw)))).isInstanceOf(RepositoryException.class);
    }

    @Test
    void selectHotGameScheduledTest() {
        Game game = defaultScheduledGame();

        gameRepository.insert(game);

        assertThat(gameRepository.selectHotGameScheduled()).isEmpty();

        game.setGameDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())));

        gameRepository.update(game);

        assertThat(gameRepository.selectHotGameScheduled()).isEqualTo(List.of(game));
    }

    @Test
    void selectByParametersTest() {
        int size = 3;
        int indexScheduledGame = 0;
        int indexInProgressGame = 1;
        int indexCompletedGame = 2;
        List<Game> gameList = generateGames(3);

        for (var game : gameList) {
            gameRepository.insert(game);
        }

        gameList.get(indexInProgressGame).setStatus(GameStatus.InProgress);
        gameList.get(indexInProgressGame).setGoalGuestTeam(0);
        gameList.get(indexInProgressGame).setGoalHomeTeam(0);
        gameList.get(indexInProgressGame).setTime(GameTime.Time1);
        gameRepository.startGame(gameList.get(indexInProgressGame).getIdGame());


        gameList.get(indexCompletedGame).setStatus(GameStatus.Completed);
        gameList.get(indexCompletedGame).setResult(GameResult.Draw);
        gameRepository.endGame(gameList.get(indexCompletedGame).getIdGame(), GameResult.Draw);

        List<Game> myListScheduled = gameList.subList(indexScheduledGame, indexInProgressGame);
        List<Game> myListInProgress = gameList.subList(indexInProgressGame, indexCompletedGame);
        List<Game> myListCompleted = gameList.subList(indexCompletedGame, size);

        List<Game> dbNothingCompletedGame = gameRepository.selectByParameters(gameList.get(indexScheduledGame), nothingCompletedGameFields);
        List<Game> dbScheduledGame = gameRepository.selectByParameters(gameList.get(indexScheduledGame), statusCompletedGameFields);
        List<Game> dbInProgressGame = gameRepository.selectByParameters(gameList.get(indexInProgressGame), statusCompletedGameFields);
        List<Game> dbCompletedGame = gameRepository.selectByParameters(gameList.get(indexCompletedGame), statusCompletedGameFields);
        List<Game> dbAllCompletedGame = gameRepository.selectByParameters(gameList.get(indexCompletedGame), allCompletedGameFields);

        assertThat(dbNothingCompletedGame).isEqualTo(gameList);
        assertThat(dbScheduledGame).isEqualTo(myListScheduled);
        assertThat(dbInProgressGame).isEqualTo(myListInProgress);
        assertThat(dbCompletedGame).isEqualTo(myListCompleted);
        assertThat(dbAllCompletedGame).isEqualTo(myListCompleted);
    }

    @Test
    void selectAllGameTest() {
        int countGame = 6;
        List<Game> games = generateGames(countGame);
        for (var game : games) {
            gameRepository.insert(game);
        }

        assertThat(gameRepository.selectAllGameScheduled()).isEqualTo(games);
        assertThat(gameRepository.selectAllGameInProgress()).isEmpty();
        assertThat(gameRepository.selectAllGameCompleted()).isEmpty();
        assertThat(gameRepository.selectAll()).isEqualTo(games);

        for (var game : games) {
            game.setStatus(GameStatus.InProgress);
            game.setGoalGuestTeam(0);
            game.setGoalHomeTeam(0);
            game.setTime(GameTime.Time1);
            gameRepository.startGame(game.getIdGame());
        }

        assertThat(gameRepository.selectAllGameScheduled()).isEmpty();
        assertThat(gameRepository.selectAllGameInProgress()).isEqualTo(games);
        assertThat(gameRepository.selectAllGameCompleted()).isEmpty();
        assertThat(gameRepository.selectAll()).isEqualTo(games);

        for (var game : games) {
            game.setTime(GameTime.Time2);
            gameRepository.startSecondHalf(game.getIdGame());
        }

        assertThat(gameRepository.selectAllGameScheduled()).isEmpty();
        assertThat(gameRepository.selectAllGameInProgress()).isEqualTo(games);
        assertThat(gameRepository.selectAllGameCompleted()).isEmpty();
        assertThat(gameRepository.selectAll()).isEqualTo(games);

        for (var game : games) {
            game.setStatus(GameStatus.Completed);
            game.setResult(GameResult.Draw);
            gameRepository.endGame(game.getIdGame(), game.getResult());
        }

        assertThat(gameRepository.selectAllGameScheduled()).isEmpty();
        assertThat(gameRepository.selectAllGameInProgress()).isEmpty();
        assertThat(gameRepository.selectAllGameCompleted()).isEqualTo(games);
        assertThat(gameRepository.selectAll()).isEqualTo(games);
    }

    @Test
    void selectLimitGameTest() {
        int limit = 5;
        int countGame = 8;
        List<Game> games = generateGames(countGame);
        List<Game> gamesLimit = games.subList(0, limit);
        for (var game : games) {
            gameRepository.insert(game);
        }
        assertThat(gameRepository.selectLimitGameScheduled()).isEqualTo(gamesLimit);
        assertThat(gameRepository.selectLimitGameInProgress()).isEmpty();
        assertThat(gameRepository.selectLimitGameCompleted()).isEmpty();

        for (var game : games) {
            game.setStatus(GameStatus.InProgress);
            game.setGoalGuestTeam(0);
            game.setGoalHomeTeam(0);
            game.setTime(GameTime.Time1);
            gameRepository.startGame(game.getIdGame());
        }
        assertThat(gameRepository.selectLimitGameScheduled()).isEmpty();
        assertThat(gameRepository.selectLimitGameInProgress()).isEqualTo(gamesLimit);
        assertThat(gameRepository.selectLimitGameCompleted()).isEmpty();

        for (var game : games) {
            game.setTime(GameTime.Time2);
            gameRepository.startSecondHalf(game.getIdGame());
        }

        assertThat(gameRepository.selectLimitGameScheduled()).isEmpty();
        assertThat(gameRepository.selectLimitGameInProgress()).isEqualTo(gamesLimit);
        assertThat(gameRepository.selectLimitGameCompleted()).isEmpty();

        for (var game : games) {
            game.setStatus(GameStatus.Completed);
            game.setResult(GameResult.Draw);
            gameRepository.endGame(game.getIdGame(), game.getResult());
        }

        assertThat(gameRepository.selectLimitGameScheduled()).isEmpty();
        assertThat(gameRepository.selectLimitGameInProgress()).isEmpty();
        assertThat(gameRepository.selectLimitGameCompleted()).isEqualTo(gamesLimit);
    }

    @Test
    void deleteOnValidId() {
        Game game = defaultScheduledGame();

        gameRepository.insert(game);

        assertThat(gameRepository.delete(game.getIdGame())).isTrue();

        assertThat(gameRepository.selectById(game.getIdGame())).isEmpty();
    }

    @Test
    void deleteOnInvalidId() {
        assertThat(gameRepository.delete(-1L)).isFalse();
    }

    @Test
    void deleteOnNullId() {
        assertThatThrownBy(() -> assertThat(gameRepository.delete(null))).isInstanceOf(RepositoryException.class);
    }

    @Test
    void updateOnValidId() {
        Game game = defaultScheduledGame();

        gameRepository.insert(game);

        game.setStatus(GameStatus.Completed);
        game.setTime(GameTime.Time1);
        game.setGameDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())));

        assertThat(gameRepository.update(game)).isTrue();

        Optional<Game> gameFromDBOptional = gameRepository.selectById(game.getIdGame());
        assertThat(gameFromDBOptional.isPresent()).isTrue();

        Game gameFromDB = gameFromDBOptional.get();
        assertThat(gameFromDB).isEqualTo(game);
    }

    @Test
    void updateDescriptionOnValidId() {
        Game game = defaultScheduledGame();

        gameRepository.insert(game);

        game.setGoalGuestTeam(2);
        game.setGoalHomeTeam(2);
        game.setCoefficientOnGuestTeam(2.4F);
        game.setCoefficientOnDraw(2.2F);
        game.setCoefficientOnGuestTeam(2.6F);

        assertThat(gameRepository.updateDescriptionGame(game)).isTrue();

        Optional<Game> gameFromDBOptional = gameRepository.selectById(game.getIdGame());
        assertThat(gameFromDBOptional.isPresent()).isTrue();

        Game gameFromDB = gameFromDBOptional.get();
        assertThat(gameFromDB).isEqualTo(game);
    }

    @Test
    void updateOnInvalidId() {
        Game game = defaultScheduledGame();
        game.setGoalGuestTeam(2);
        game.setGoalHomeTeam(2);
        assertThat(gameRepository.update(game)).isFalse();
        assertThat(gameRepository.updateDescriptionGame(game)).isFalse();
    }

    @Test
    void updateOnNullId() {
        assertThatThrownBy(() -> assertThat(gameRepository.update(null))).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> assertThat(gameRepository.updateDescriptionGame(null))).isInstanceOf(RepositoryException.class);
    }
}