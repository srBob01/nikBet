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
import ru.arsentiev.processing.query.UserQueryCreator;
import ru.arsentiev.repository.GameRepository;
import ru.arsentiev.repository.PredictionRepository;
import ru.arsentiev.repository.TeamRepository;
import ru.arsentiev.repository.UserRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PredictionRepositoryTest {
    private final Team team1;
    private final Team team2;
    private final Game game1;
    private final Game game2;
    private final User user1;
    private final User user2;
    private final TestConnectionGetter connectionGetter = TestConnectionGetter.getInstance();
    private final TeamRepository teamRepository = new TeamRepository(connectionGetter);
    private final GameQueryCreator gameQueryCreator = new GameQueryCreator();
    private final GameRepository gameRepository = new GameRepository(connectionGetter, teamRepository, gameQueryCreator);
    private final UserQueryCreator userQueryCreator = new UserQueryCreator();
    private final UserRepository userRepository = new UserRepository(connectionGetter, userQueryCreator);
    private final PredictionRepository predictionRepository = new PredictionRepository(connectionGetter, gameRepository, userRepository);

    {
        team1 = Team.builder()
                .title("Liverpool")
                .abbreviation("LIV")
                .build();

        team2 = Team.builder()
                .title("Chelsea")
                .abbreviation("CHE")
                .build();

        game1 = Game.builder()
                .guestTeam(team2)
                .homeTeam(team1)
                .gameDate(LocalDateTime.of(LocalDate.of(2024, 4, 22), LocalTime.of(15, 0, 0)))
                .status(GameStatus.Scheduled)
                .coefficientOnDraw(2.3F)
                .coefficientOnHomeTeam(3.5F)
                .coefficientOnGuestTeam(4.4F)
                .build();

        game2 = Game.builder()
                .guestTeam(team1)
                .homeTeam(team2)
                .gameDate(LocalDateTime.of(LocalDate.of(2024, 4, 22), LocalTime.of(15, 0, 0)))
                .status(GameStatus.Scheduled)
                .coefficientOnDraw(1.3F)
                .coefficientOnHomeTeam(3.3F)
                .coefficientOnGuestTeam(3.4F)
                .build();

        user1 = User.builder()
                .nickname("john_doe")
                .firstName("John")
                .lastName("Doe")
                .patronymic("Smith")
                .password("password123")
                .salt("abc123")
                .phoneNumber("+79969291133")
                .email("john.doe@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .accountBalance(BigDecimal.valueOf(0))
                .role(UserRole.USER)
                .build();

        user2 = User.builder()
                .nickname("user_bro")
                .firstName("user")
                .lastName("bro")
                .password("password1234")
                .salt("abc1234")
                .phoneNumber("+79969291144")
                .email("john4doe@example.com")
                .birthDate(LocalDate.of(1990, 5, 15))
                .accountBalance(BigDecimal.valueOf(0))
                .role(UserRole.USER)
                .build();
    }

    private Prediction defaultNotBetPrediction(User user, Game game, BigDecimal summa) {
        return Prediction.builder()
                .user(user)
                .game(game)
                .predictionStatus(PredictionStatus.BetNotPlayed)
                .coefficient(game.getCoefficientOnHomeTeam())
                .prediction(GameResult.HomeWin)
                .predictionDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())))
                .summa(summa)
                .build();
    }

    private Stream<List<Prediction>> generateValidPredictionListByUserId() {
        Prediction prediction1 = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(10));

        Prediction prediction2 = defaultNotBetPrediction(user1, game2, BigDecimal.valueOf(30));

        Prediction prediction3 = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(20));

        Prediction prediction4 = defaultNotBetPrediction(user1, game2, BigDecimal.valueOf(40));

        Prediction prediction5 = defaultNotBetPrediction(user2, game1, BigDecimal.valueOf(50));

        Prediction prediction6 = defaultNotBetPrediction(user2, game2, BigDecimal.valueOf(120));

        Prediction prediction7 = defaultNotBetPrediction(user2, game1, BigDecimal.valueOf(2560));

        Prediction prediction8 = defaultNotBetPrediction(user2, game2, BigDecimal.valueOf(5550));

        return Stream.of(List.of(prediction1, prediction2, prediction3, prediction4), List.of(prediction5, prediction6, prediction7, prediction8));
    }

    private Stream<List<Prediction>> generateValidPredictionListByGameId() {
        Prediction prediction1 = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(10));

        Prediction prediction2 = defaultNotBetPrediction(user1, game2, BigDecimal.valueOf(30));

        Prediction prediction3 = defaultNotBetPrediction(user2, game1, BigDecimal.valueOf(20));

        Prediction prediction4 = defaultNotBetPrediction(user2, game2, BigDecimal.valueOf(50));

        return Stream.of(List.of(prediction1, prediction3), List.of(prediction2, prediction4));
    }

    private Stream<List<Prediction>> generateValidPredictionList() {
        Prediction prediction1 = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(10));

        Prediction prediction2 = defaultNotBetPrediction(user1, game2, BigDecimal.valueOf(30));

        Prediction prediction3 = defaultNotBetPrediction(user2, game1, BigDecimal.valueOf(20));

        Prediction prediction4 = defaultNotBetPrediction(user2, game2, BigDecimal.valueOf(50));

        return Stream.of(List.of(prediction1, prediction2), List.of(prediction3, prediction4, prediction1),
                List.of(prediction1, prediction2, prediction3, prediction4));
    }

    private Stream<Prediction> generateInValidPrediction() {
        Prediction prediction1 = Prediction.builder()
                .game(game1)
                .predictionStatus(PredictionStatus.BetNotPlayed)
                .coefficient(game1.getCoefficientOnHomeTeam())
                .prediction(GameResult.HomeWin)
                .predictionDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())))
                .summa(BigDecimal.valueOf(20))
                .build();

        Prediction prediction2 = Prediction.builder()
                .user(user1)
                .game(game1)
                .predictionDate(LocalDateTime.of(LocalDate.now(), LocalTime.of(LocalTime.now().getHour(), LocalTime.now().getMinute())))
                .summa(BigDecimal.valueOf(10))
                .build();

        return Stream.of(prediction1, prediction2);
    }

    @SneakyThrows
    @BeforeAll
    public void insertInfoForPrediction() {
        try (Connection connection = connectionGetter.get()) {
            //language=PostgreSQL
            String CLEAR_TABLES = "TRUNCATE nikbet_test.public.teams RESTART IDENTITY CASCADE; " +
                                  "TRUNCATE nikbet_test.public.users RESTART IDENTITY CASCADE; " +
                                  "TRUNCATE nikbet_test.public.games RESTART IDENTITY CASCADE;";
            connection.prepareStatement(CLEAR_TABLES).executeUpdate();
        }

        teamRepository.insert(team1);
        teamRepository.insert(team2);

        gameRepository.insert(game1);
        gameRepository.insert(game2);

        userRepository.insert(user1);
        userRepository.insert(user2);
        user1.setSalt(null);
        user1.setPassword(null);
        user2.setSalt(null);
        user2.setPassword(null);
    }

    @SneakyThrows
    @BeforeEach
    public void clear() {
        try (Connection connection = connectionGetter.get()) {
            //language=PostgreSQL
            String CLEAR_TABLE = "TRUNCATE nikbet_test.public.predictions RESTART IDENTITY CASCADE";
            connection.prepareStatement(CLEAR_TABLE).executeUpdate();
        }
    }


    @Test
    void insertValidPredictionTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(2));

        assertThat(predictionRepository.insert(prediction)).isTrue();

        Optional<Prediction> predictionFromDBOptional = predictionRepository.selectById(prediction.getIdPrediction());

        assertThat(predictionFromDBOptional.isPresent()).isTrue();

        Prediction predictionFromDB = predictionFromDBOptional.get();

        assertThat(predictionFromDB).isEqualTo(prediction);
    }

    @ParameterizedTest
    @MethodSource("generateInValidPrediction")
    void insertInvalidPredictionTest(Prediction prediction) {
        assertThatThrownBy(() -> predictionRepository.insert(prediction)).isInstanceOf(RepositoryException.class);
    }

    @ParameterizedTest
    @MethodSource("generateValidPredictionList")
    void selectAllPredictionTest(List<Prediction> predictions) {
        for (var prediction : predictions) {
            predictionRepository.insert(prediction);
        }
        List<Prediction> predictionsFromDb = predictionRepository.selectAll();
        assertThat(predictionsFromDb).isEqualTo(predictions);
    }

    @ParameterizedTest
    @MethodSource("generateValidPredictionList")
    void updatePredictionStatusOfListTest(List<Prediction> predictions) {
        for (var prediction : predictions) {
            predictionRepository.insert(prediction);
        }

        List<Long> listId = predictions.stream().map(Prediction::getIdPrediction).toList();

        for (var prediction : predictions) {
            prediction.setPredictionStatus(PredictionStatus.BetPlayed);
        }

        predictionRepository.updatePredictionStatusOfList(listId);

        List<Prediction> predictionsFromDb = predictionRepository.selectAll();
        assertThat(predictionsFromDb).isEqualTo(predictions);
    }

    @ParameterizedTest
    @MethodSource("generateValidPredictionListByUserId")
    void selectByValidUserIdTest(List<Prediction> predictions) {
        List<Prediction> predictionSubList = predictions.subList(0, 3);
        long idUser = predictions.get(0).getUser().getIdUser();
        for (var prediction : predictions) {
            predictionRepository.insert(prediction);
        }

        List<Prediction> predictionBetNotPlayedListFromDbById1 = predictionRepository.selectByUserIdBetNotPlayed(idUser);
        List<Prediction> predictionBetPlayedListFromDbById1 = predictionRepository.selectByUserIdBetPlayed(idUser);
        List<Prediction> predictionBetNotPlayedListFromDbByIdLimit1 = predictionRepository.selectByUserIdLimitBetNotPlayed(idUser);
        List<Prediction> predictionBetPlayedListFromDbByIdLimit1 = predictionRepository.selectByUserIdLimitBetPlayed(idUser);

        assertThat(predictionBetNotPlayedListFromDbById1).isEqualTo(predictions);
        assertThat(predictionBetPlayedListFromDbById1).isEmpty();
        assertThat(predictionBetNotPlayedListFromDbByIdLimit1).isEqualTo(predictionSubList);
        assertThat(predictionBetPlayedListFromDbByIdLimit1).isEmpty();

        for (var prediction : predictions) {
            prediction.setPredictionStatus(PredictionStatus.BetPlayed);
        }

        predictionRepository.updatePredictionStatusOfList(predictions.stream()
                .map(Prediction::getIdPrediction)
                .collect(Collectors.toList()));

        List<Prediction> predictionBetNotPlayedListFromDbById2 = predictionRepository.selectByUserIdBetNotPlayed(idUser);
        List<Prediction> predictionBetPlayedListFromDbById2 = predictionRepository.selectByUserIdBetPlayed(idUser);
        List<Prediction> predictionBetNotPlayedListFromDbByIdLimit2 = predictionRepository.selectByUserIdLimitBetNotPlayed(idUser);
        List<Prediction> predictionBetPlayedListFromDbByIdLimit2 = predictionRepository.selectByUserIdLimitBetPlayed(idUser);

        assertThat(predictionBetNotPlayedListFromDbById2).isEmpty();
        assertThat(predictionBetPlayedListFromDbById2).isEqualTo(predictions);
        assertThat(predictionBetNotPlayedListFromDbByIdLimit2).isEmpty();
        assertThat(predictionBetPlayedListFromDbByIdLimit2).isEqualTo(predictionSubList);
    }

    @Test
    void selectByInValidUserIdTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(1));
        predictionRepository.insert(prediction);

        List<Prediction> predictionBetNotPlayedListFromDbById = predictionRepository.selectByUserIdBetNotPlayed(user2.getIdUser());
        List<Prediction> predictionBetNotPlayedLimitListFromDbById = predictionRepository.selectByUserIdLimitBetNotPlayed(user2.getIdUser());
        assertThat(predictionBetNotPlayedListFromDbById).isEmpty();
        assertThat(predictionBetNotPlayedLimitListFromDbById).isEmpty();

        predictionRepository.updatePredictionStatusOfList(List.of(prediction.getIdPrediction()));

        List<Prediction> predictionBetPlayedListFromDbById = predictionRepository.selectByUserIdBetPlayed(user2.getIdUser());
        List<Prediction> predictionBetLimitPlayedListFromDbById = predictionRepository.selectByUserIdLimitBetPlayed(user2.getIdUser());
        assertThat(predictionBetPlayedListFromDbById).isEmpty();
        assertThat(predictionBetLimitPlayedListFromDbById).isEmpty();
    }

    @Test
    void selectByInNullUserIdTest() {
        assertThatThrownBy(() -> predictionRepository.selectByUserIdBetNotPlayed(null)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> predictionRepository.selectByUserIdLimitBetNotPlayed(null)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> predictionRepository.selectByUserIdBetPlayed(null)).isInstanceOf(RepositoryException.class);
        assertThatThrownBy(() -> predictionRepository.selectByUserIdLimitBetPlayed(null)).isInstanceOf(RepositoryException.class);

    }

    @Test
    void selectByValidIdTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(1));
        predictionRepository.insert(prediction);

        Optional<Prediction> optionalPredictionFromDB = predictionRepository.selectById(prediction.getIdPrediction());
        assertThat(optionalPredictionFromDB.isPresent()).isTrue();

        Prediction predictionFromDB = optionalPredictionFromDB.get();

        assertThat(predictionFromDB).isEqualTo(prediction);
    }

    @Test
    void selectByInvalidIdTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(1));
        predictionRepository.insert(prediction);

        Optional<Prediction> optionalPredictionFromDB = predictionRepository.selectById(prediction.getIdPrediction() + 1);
        assertThat(optionalPredictionFromDB.isPresent()).isFalse();
    }

    @Test
    void selectByNullIdTest() {
        assertThatThrownBy(() -> predictionRepository.selectById(null)).isInstanceOf(RepositoryException.class);
    }

    @ParameterizedTest
    @MethodSource("generateValidPredictionListByGameId")
    void selectByGameValidIdTest(List<Prediction> predictions) {
        long idGame = predictions.get(0).getGame().getIdGame();
        for (var prediction : predictions) {
            predictionRepository.insert(prediction);
        }

        List<Prediction> predictionListFromDB = predictionRepository.selectByGameId(idGame);
        assertThat(predictionListFromDB).isEqualTo(predictions);
    }

    @Test
    void selectByInvalidGameIdTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(1));
        predictionRepository.insert(prediction);

        List<Prediction> predictionList = predictionRepository.selectByGameId(game1.getIdGame() + 1);
        assertThat(predictionList).isEmpty();
    }

    @Test
    void selectByNullGameIdTest() {
        assertThatThrownBy(() -> predictionRepository.selectByGameId(null)).isInstanceOf(RepositoryException.class);
    }

    @Test
    void updateValidPredictionTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(1));
        predictionRepository.insert(prediction);

        prediction.setUser(user2);
        assertThat(predictionRepository.update(prediction)).isTrue();

        Optional<Prediction> optionalPrediction = predictionRepository.selectById(prediction.getIdPrediction());
        assertThat(optionalPrediction.isPresent()).isTrue();
        Prediction predictionFRomDB = optionalPrediction.get();

        assertThat(predictionFRomDB).isEqualTo(prediction);
    }

    @Test
    void updateInvalidPredictionTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(1));
        predictionRepository.insert(prediction);

        prediction.setIdPrediction(2L);
        assertThat(predictionRepository.update(prediction)).isFalse();
    }

    @Test
    void updateNullPredictionTest() {
        assertThatThrownBy(() -> predictionRepository.update(null)).isInstanceOf(RepositoryException.class);
    }

    @Test
    void deleteValidPredictionTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(1));
        predictionRepository.insert(prediction);

        assertThat(predictionRepository.delete(prediction.getIdPrediction())).isTrue();

        assertThat(predictionRepository.selectById(prediction.getIdPrediction())).isEmpty();
    }

    @Test
    void deleteInvalidPredictionTest() {
        Prediction prediction = defaultNotBetPrediction(user1, game1, BigDecimal.valueOf(1));
        predictionRepository.insert(prediction);
        prediction.setIdPrediction(2L);

        assertThat(predictionRepository.delete(prediction.getIdPrediction())).isFalse();
    }

    @Test
    void deleteNullPredictionTest() {
        assertThatThrownBy(() -> predictionRepository.delete(null)).isInstanceOf(RepositoryException.class);
    }

}