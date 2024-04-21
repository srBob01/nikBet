package ru.arsentiev.repository;

import ru.arsentiev.entity.*;
import ru.arsentiev.exception.DaoException;
import ru.arsentiev.singleton.connection.ConnectionManager;
import ru.arsentiev.singleton.query.GameQueryCreator;
import ru.arsentiev.singleton.query.entity.CompletedGameFields;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameDao implements BaseDao<Long, Game> {
    private final TeamDao teamDAO;
    private final ConnectionManager connectionManager;
    private final GameQueryCreator gameQueryCreator;

    public GameDao(ConnectionManager connectionManager, TeamDao teamDAO, GameQueryCreator gameQueryCreator) {
        this.connectionManager = connectionManager;
        this.teamDAO = teamDAO;
        this.gameQueryCreator = gameQueryCreator;
    }

    //language=PostgreSQL
    private static final String INSERT_GAME = "INSERT INTO games " +
                                              "(idHomeTeam, idGuestTeam, goalHomeTeam, goalGuestTeam," +
                                              " gameDate, status, coefficientOnHomeTeam, coefficientOnDraw," +
                                              " coefficientOnGuestTeam, time, result) " +
                                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
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
    private static final String DELETE_GAME = "DELETE FROM games WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String UPDATE_GAME = "UPDATE games SET " +
                                              "idHomeTeam = ?, idGuestTeam = ?, goalHomeTeam = ?, goalGuestTeam = ?, gameDate = ?, status = ?, " +
                                              "coefficientOnHomeTeam = ?, coefficientOnDraw = ?, coefficientOnGuestTeam = ?, time = ?, result = ? WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String UPDATE_COEFFICIENT_GAME = "UPDATE games SET " +
                                                          "coefficientOnHomeTeam = ?, coefficientOnDraw = ?, coefficientOnGuestTeam = ? WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String UPDATE_GOALS_GAME = "UPDATE games SET " +
                                                    "goalHomeTeam = ?, goalGuestTeam = ? WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String UPDATE_RESULT_STATUS_GAME = "UPDATE games SET " +
                                                            "status = ?, result = ? WHERE idGame = ?;";

    @Override
    public Game insert(Game game) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_GAME, Statement.RETURN_GENERATED_KEYS)) {

            setStatement(game, preparedStatement);

            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                game.setIdGame(generatedKeys.getLong(1));
            }
            return game;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
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
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(select);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                games.add(mapResultSetToGame(resultSet));
            }
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
        return games;
    }

    @Override
    public Optional<Game> selectById(Long id) {
        try (Connection connection = connectionManager.get()) {
            return selectById(id, connection);
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
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
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<Game> selectByParameters(Game game, CompletedGameFields completedGameFields) {
        String sql = gameQueryCreator.createGameSelectQuery(game, completedGameFields);
        return getGames(sql);
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_GAME)) {
            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Game game) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GAME)) {

            setStatement(game, preparedStatement);
            preparedStatement.setLong(11, game.getIdGame());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public boolean updateCoefficients(Long idGame, Float coefficientOnHomeTeam, Float coefficientOnDraw, Float coefficientOnGuestTeam) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_COEFFICIENT_GAME)) {

            preparedStatement.setFloat(1, coefficientOnHomeTeam);
            preparedStatement.setFloat(2, coefficientOnDraw);
            preparedStatement.setFloat(3, coefficientOnGuestTeam);
            preparedStatement.setLong(4, idGame);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public boolean updateGoals(Long idGame, Integer goalHomeTeam, Integer goalGuestTeam) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_GOALS_GAME)) {

            preparedStatement.setInt(1, goalHomeTeam);
            preparedStatement.setInt(2, goalGuestTeam);
            preparedStatement.setLong(3, idGame);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public boolean updateResultAndStatus(Long idGame, GameStatus status, GameResult result) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RESULT_STATUS_GAME)) {

            preparedStatement.setString(1, status.name());
            preparedStatement.setString(2, result.name());
            preparedStatement.setLong(3, idGame);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }


    private void setStatement(Game game, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(1, game.getHomeTeam().getIdTeam());
        preparedStatement.setLong(2, game.getGuestTeam().getIdTeam());
        preparedStatement.setInt(3, game.getGoalHomeTeam());
        preparedStatement.setInt(4, game.getGoalGuestTeam());
        preparedStatement.setTimestamp(5, Timestamp.valueOf(game.getGameDate()));
        preparedStatement.setString(6, game.getStatus().name());
        preparedStatement.setFloat(7, game.getCoefficientOnHomeTeam());
        preparedStatement.setFloat(8, game.getCoefficientOnDraw());
        preparedStatement.setFloat(9, game.getCoefficientOnGuestTeam());
        preparedStatement.setString(10, game.getResult().name());
    }

    private Game mapResultSetToGame(ResultSet resultSet) throws SQLException {
        Long idGame = resultSet.getLong("idGame");
        Team homeTeam = teamDAO.selectById(resultSet.getLong("idHomeTeam")
                , resultSet.getStatement().getConnection()).orElseThrow(() -> new SQLException("HomeTeam not found"));
        Team guestTeam = teamDAO.selectById(resultSet.getLong("idGuestTeam")
                , resultSet.getStatement().getConnection()).orElseThrow(() -> new SQLException("GuestTeam not found"));
        Integer goalHomeTeam = resultSet.getObject("goalHomeTeam", Integer.class);
        Integer goalGuestTeam = resultSet.getObject("goalGuestTeam", Integer.class);
        LocalDateTime gameDate = resultSet.getTimestamp("gameDate").toLocalDateTime();
        GameStatus status = GameStatus.valueOf(resultSet.getString("status"));
        Float coefficientOnHomeTeam = resultSet.getBigDecimal("coefficientOnHomeTeam") != null ? resultSet.getBigDecimal("coefficientOnHomeTeam").floatValue() : null;
        Float coefficientOnDraw = resultSet.getBigDecimal("coefficientOnDraw") != null ? resultSet.getBigDecimal("coefficientOnDraw").floatValue() : null;
        Float coefficientOnGuestTeam = resultSet.getBigDecimal("coefficientOnGuestTeam") != null ? resultSet.getBigDecimal("coefficientOnGuestTeam").floatValue() : null;
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
