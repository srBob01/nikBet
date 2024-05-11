package ru.arsentiev.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.arsentiev.entity.*;
import ru.arsentiev.exception.RepositoryException;
import ru.arsentiev.processing.connection.ConnectionGetter;
import ru.arsentiev.processing.query.GameQueryCreator;
import ru.arsentiev.processing.query.entity.CompletedGameFields;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameRepository implements BaseRepository<Long, Game> {
    private static final Logger logger = LogManager.getLogger();
    private final TeamRepository teamRepository;
    private final ConnectionGetter connectionGetter;
    private final GameQueryCreator gameQueryCreator;

    public GameRepository(ConnectionGetter connectionGetter, TeamRepository teamRepository, GameQueryCreator gameQueryCreator) {
        this.connectionGetter = connectionGetter;
        this.teamRepository = teamRepository;
        this.gameQueryCreator = gameQueryCreator;
    }

    //language=PostgreSQL
    private static final String INSERT_GAME = "INSERT INTO games " +
                                              "(idHomeTeam, idGuestTeam, gameDate, coefficientOnHomeTeam," +
                                              " coefficientOnDraw, coefficientOnGuestTeam) " +
                                              "VALUES (?, ?, ?, ?, ?, ?);";
    //language=PostgreSQL
    private static final String SELECT_GAME_BY_ID = "SELECT idgame, idhometeam, idguestteam, goalhometeam," +
                                                    " goalguestteam, gamedate, status, coefficientonhometeam," +
                                                    " coefficientOnDraw, coefficientonguestteam, time, result" +
                                                    " FROM games WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String SELECT_ALL_GAMES = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
                                                   " gamedate, status, coefficientonhometeam, coefficientOnDraw," +
                                                   " coefficientonguestteam, time, result" +
                                                   " FROM games;";
    //language=PostgreSQL
    private static final String SELECT_SCHEDULED_GAMES = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
                                                         " gamedate, status, coefficientonhometeam, coefficientOnDraw," +
                                                         " coefficientonguestteam, time, result" +
                                                         " FROM games WHERE status = 'Scheduled'" +
                                                         " ORDER BY gamedate;";
    //language=PostgreSQL
    private static final String SELECT_IN_PROGRESS_GAMES = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
                                                           " gamedate, status, coefficientonhometeam, coefficientOnDraw," +
                                                           " coefficientonguestteam, time, result" +
                                                           " FROM games WHERE status = 'InProgress'" +
                                                           " ORDER BY gamedate;";
    //language=PostgreSQL
    private static final String SELECT_COMPLETED_GAMES = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
                                                         " gamedate, status, coefficientonhometeam, coefficientOnDraw," +
                                                         " coefficientonguestteam, time, result" +
                                                         " FROM games WHERE status = 'Completed'" +
                                                         " ORDER BY gamedate;";
    //language=PostgreSQL
    private static final String SELECT_SCHEDULED_GAMES_LIMIT = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
                                                               " gamedate, status, coefficientonhometeam, coefficientOnDraw," +
                                                               " coefficientonguestteam, time, result" +
                                                               " FROM games WHERE status = 'Scheduled'" +
                                                               " ORDER BY gamedate LIMIT 5;";
    //language=PostgreSQL
    private static final String SELECT_IN_PROGRESS_GAMES_LIMIT = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
                                                                 " gamedate, status, coefficientonhometeam, coefficientOnDraw," +
                                                                 " coefficientonguestteam, time, result" +
                                                                 " FROM games WHERE status = 'InProgress'" +
                                                                 " ORDER BY gamedate LIMIT 5;";
    //language=PostgreSQL
    private static final String SELECT_COMPLETED_GAMES_LIMIT = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
                                                               " gamedate, status, coefficientonhometeam, coefficientOnDraw," +
                                                               " coefficientonguestteam, time, result" +
                                                               " FROM games WHERE status = 'Completed'" +
                                                               " ORDER BY gamedate LIMIT 5;";

    //language=PostgreSQL
    private static final String SELECT_HOT_GAMES_LIMIT = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
                                                         " gamedate, status, coefficientonhometeam, coefficientOnDraw," +
                                                         " coefficientonguestteam, time, result" +
                                                         " FROM games WHERE " +
                                                         " status = 'Scheduled' AND" +
                                                         " gamedate BETWEEN NOW() - INTERVAL '10 minutes' AND NOW() + INTERVAL '10 minutes'" +
                                                         " ORDER BY gamedate;";
    //language=PostgreSQL
    private static final String DELETE_GAME = "DELETE FROM games WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String UPDATE_GAME = "UPDATE games SET " +
                                              "idHomeTeam = ?, idGuestTeam = ?, goalHomeTeam = ?, goalGuestTeam = ?, gameDate = ?, status = ?, " +
                                              "coefficientOnHomeTeam = ?, coefficientOnDraw = ?, coefficientOnGuestTeam = ?, time = ?, result = ? WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String UPDATE_DESCRIPTION_GAME = "UPDATE games SET" +
                                                          " coefficientOnHomeTeam = ?, coefficientOnDraw = ?, coefficientOnGuestTeam = ?," +
                                                          " goalHomeTeam = ?, goalGuestTeam = ? WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String START_GAME = "UPDATE games SET " +
                                             "status = 'InProgress', time = 'Time1'," +
                                             " goalguestteam = 0, goalhometeam = 0" +
                                             " WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String START_SECOND_HALF = "UPDATE games SET" +
                                                    " time = 'Time2'" +
                                                    " WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String END_GAME = "UPDATE games SET status = 'Completed', result = ? " +
                                           " WHERE idGame = ?;";


    @Override
    public boolean insert(Game game) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAME, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, game.getHomeTeam().getIdTeam());
            preparedStatement.setLong(2, game.getGuestTeam().getIdTeam());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(game.getGameDate()));
            preparedStatement.setFloat(4, game.getCoefficientOnHomeTeam());
            preparedStatement.setFloat(5, game.getCoefficientOnDraw());
            preparedStatement.setFloat(6, game.getCoefficientOnGuestTeam());

            boolean res = preparedStatement.executeUpdate() > 0;
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                game.setIdGame(generatedKeys.getLong(1));
            }
            return res;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to insert game: " + game.toString() + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<Game> selectAll() {
        return getGames(SELECT_ALL_GAMES);
    }

    public List<Game> selectLimitGameScheduled() {
        return getGamesScheduled(SELECT_SCHEDULED_GAMES_LIMIT);
    }

    public List<Game> selectLimitGameInProgress() {
        return getGamesInProgress(SELECT_IN_PROGRESS_GAMES_LIMIT);
    }

    public List<Game> selectLimitGameCompleted() {
        return getGamesCompleted(SELECT_COMPLETED_GAMES_LIMIT);
    }

    public List<Game> selectHotGameScheduled() {
        return getGamesCompleted(SELECT_HOT_GAMES_LIMIT);
    }

    public List<Game> selectAllGameScheduled() {
        return getGamesScheduled(SELECT_SCHEDULED_GAMES);
    }

    public List<Game> selectAllGameInProgress() {
        return getGamesInProgress(SELECT_IN_PROGRESS_GAMES);
    }

    public List<Game> selectAllGameCompleted() {
        return getGamesCompleted(SELECT_COMPLETED_GAMES);
    }

    private List<Game> getGamesCompleted(String select) {
        return getGames(select);
    }

    private List<Game> getGamesInProgress(String select) {
        return getGames(select);
    }

    private List<Game> getGamesScheduled(String select) {
        return getGames(select);
    }

    private List<Game> getGames(String select) {
        List<Game> games = new ArrayList<>();
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(select);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                games.add(mapResultSetToGame(resultSet));
            }
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select games. Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
        return games;
    }

    @Override
    public Optional<Game> selectById(Long id) {
        try (Connection connection = connectionGetter.get()) {
            return selectById(id, connection);
        } catch (SQLException | InterruptedException e) {
            logger.error("Failed to select game by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public Optional<Game> selectById(Long id, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAME_BY_ID)) {
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            Game game = null;
            if (resultSet.next()) {
                game = mapResultSetToGame(resultSet);
            }
            return Optional.ofNullable(game);
        } catch (SQLException | NullPointerException e) {
            logger.error("Failed to select game by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public List<Game> selectByParameters(Game game, CompletedGameFields completedGameFields) {
        String sql = gameQueryCreator.createGameSelectQuery(game, completedGameFields);
        return getGames(sql);
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GAME)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to delete game by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean update(Game game) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GAME)) {

            setStatement(game, preparedStatement);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to update game : " + (game != null ? game.toString() : "null") + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public boolean updateDescriptionGame(Game game) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DESCRIPTION_GAME)) {

            preparedStatement.setFloat(1, game.getCoefficientOnHomeTeam());
            preparedStatement.setFloat(2, game.getCoefficientOnDraw());
            preparedStatement.setFloat(3, game.getCoefficientOnGuestTeam());
            preparedStatement.setInt(4, game.getGoalHomeTeam());
            preparedStatement.setInt(5, game.getGoalGuestTeam());
            preparedStatement.setLong(6, game.getIdGame());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to update description game : " + (game != null ? game.toString() : "null") + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public boolean startGame(Long idGame) {
        return actionWithGame(idGame, START_GAME);
    }

    public boolean startSecondHalf(Long idGame) {
        return actionWithGame(idGame, START_SECOND_HALF);
    }

    private boolean actionWithGame(Long idGame, String sql) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, idGame);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to action with game with id : " + idGame + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public boolean endGame(Long idGame, GameResult result) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(END_GAME)) {

            preparedStatement.setString(1, result.name());
            preparedStatement.setLong(2, idGame);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to end game with id : " + idGame + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }


    private void setStatement(Game game, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(1, game.getHomeTeam().getIdTeam());
        preparedStatement.setLong(2, game.getGuestTeam().getIdTeam());
        if (game.getGoalHomeTeam() == null) {
            preparedStatement.setNull(3, Types.INTEGER);
        } else {
            preparedStatement.setInt(3, game.getGoalHomeTeam());
        }

        if (game.getGoalGuestTeam() == null) {
            preparedStatement.setNull(4, Types.INTEGER);
        } else {
            preparedStatement.setInt(4, game.getGoalGuestTeam());
        }

        preparedStatement.setTimestamp(5, Timestamp.valueOf(game.getGameDate()));
        preparedStatement.setString(6, game.getStatus().name());
        preparedStatement.setFloat(7, game.getCoefficientOnHomeTeam());
        preparedStatement.setFloat(8, game.getCoefficientOnDraw());
        preparedStatement.setFloat(9, game.getCoefficientOnGuestTeam());

        if (game.getTime() == null) {
            preparedStatement.setNull(10, Types.VARCHAR);
        } else {
            preparedStatement.setString(10, game.getTime().name());
        }

        if (game.getResult() == null) {
            preparedStatement.setNull(11, Types.VARCHAR);
        } else {
            preparedStatement.setString(11, game.getResult().name());
        }

        preparedStatement.setLong(12, game.getIdGame());
    }

    private Game mapResultSetToGame(ResultSet resultSet) throws SQLException {
        long idGame = resultSet.getLong("idGame");
        Team homeTeam = teamRepository.selectById(resultSet.getLong("idHomeTeam")
                , resultSet.getStatement().getConnection()).orElseThrow(() -> new SQLException("HomeTeam not found"));
        Team guestTeam = teamRepository.selectById(resultSet.getLong("idGuestTeam")
                , resultSet.getStatement().getConnection()).orElseThrow(() -> new SQLException("GuestTeam not found"));
        String stringGoalHomeTeam = resultSet.getString("goalHomeTeam");
        String stringGoalGuestTeam = resultSet.getString("goalGuestTeam");
        Integer goalHomeTeam = stringGoalHomeTeam != null ? Integer.parseInt(stringGoalHomeTeam) : null;
        Integer goalGuestTeam = stringGoalGuestTeam != null ? Integer.parseInt(stringGoalGuestTeam) : null;
        LocalDateTime gameDate = resultSet.getTimestamp("gameDate").toLocalDateTime();
        GameStatus status = GameStatus.valueOf(resultSet.getString("status"));
        float coefficientOnHomeTeam = resultSet.getBigDecimal("coefficientOnHomeTeam") != null ? resultSet.getBigDecimal("coefficientOnHomeTeam").floatValue() : 0;
        float coefficientOnDraw = resultSet.getBigDecimal("coefficientOnDraw") != null ? resultSet.getBigDecimal("coefficientOnDraw").floatValue() : 0;
        float coefficientOnGuestTeam = resultSet.getBigDecimal("coefficientOnGuestTeam") != null ? resultSet.getBigDecimal("coefficientOnGuestTeam").floatValue() : 0;
        String timeValue = resultSet.getString("time");
        GameTime time = timeValue != null ? GameTime.valueOf(timeValue) : null;
        String resultValue = resultSet.getString("result");
        GameResult result = resultValue != null ? GameResult.valueOf(resultValue) : null;
        return Game.builder()
                .idGame(idGame)
                .homeTeam(homeTeam)
                .guestTeam(guestTeam)
                .goalHomeTeam(goalHomeTeam)
                .goalGuestTeam(goalGuestTeam)
                .gameDate(gameDate)
                .status(status)
                .coefficientOnHomeTeam(coefficientOnHomeTeam)
                .coefficientOnDraw(coefficientOnDraw)
                .coefficientOnGuestTeam(coefficientOnGuestTeam)
                .time(time)
                .result(result)
                .build();
    }
}
