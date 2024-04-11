package ru.arsentiev.repository;

import ru.arsentiev.entity.*;
import ru.arsentiev.exception.DaoException;
import ru.arsentiev.singleton.connection.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameDao implements BaseDao<Long, Game> {
    private final TeamDao teamDAO;
    private final ConnectionManager connectionManager;

    public GameDao(ConnectionManager connectionManager, TeamDao teamDAO) {
        this.connectionManager = connectionManager;
        this.teamDAO = teamDAO;
    }

    //language=PostgreSQL
    private static final String INSERT_GAME = "INSERT INTO games " +
            "(idHomeTeam, idGuestTeam, goalHomeTeam, goalGuestTeam," +
            " gameDate, status, coefficientOnHomeTeam, coefficientOnDraw, coefficientOnGuestTeam, result) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
    //language=PostgreSQL
    private static final String SELECT_GAME_BY_ID = "SELECT idgame, idhometeam, idguestteam, goalhometeam," +
            " goalguestteam, gamedate, status, coefficientonhometeam, coefficientOnDraw, coefficientonguestteam, result" +
            " FROM games WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String SELECT_ALL_GAMES = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
            " gamedate, status, coefficientonhometeam, coefficientOnDraw, coefficientonguestteam, result" +
            " FROM games;";
    //language=PostgreSQL
    private static final String SELECT_GAMES_BY_TEAM_ID = "SELECT idgame, idhometeam, idguestteam, goalhometeam, goalguestteam," +
            " gamedate, status, coefficientonhometeam, coefficientondraw, coefficientonguestteam, result" +
            " FROM games WHERE idHomeTeam = ? OR idGuestTeam = ?;";
    //language=PostgreSQL
    private static final String DELETE_GAME = "DELETE FROM games WHERE idGame = ?;";
    //language=PostgreSQL
    private static final String UPDATE_GAME = "UPDATE games SET " +
            "idHomeTeam = ?, idGuestTeam = ?, goalHomeTeam = ?, goalGuestTeam = ?, gameDate = ?, status = ?, " +
            "coefficientOnHomeTeam = ?, coefficientOnDraw = ?, coefficientOnGuestTeam = ?, result = ? WHERE idGame = ?;";
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
        List<Game> games = new ArrayList<>();
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_GAMES);
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

    public List<Game> selectByTeamId(Long teamId) {
        List<Game> games = new ArrayList<>();
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_GAMES_BY_TEAM_ID)) {

            preparedStatement.setLong(1, teamId);
            preparedStatement.setLong(2, teamId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    games.add(mapResultSetToGame(resultSet));
                }
            }
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
        return games;
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
        Integer goalHomeTeam = resultSet.getInt("goalHomeTeam");
        Integer goalGuestTeam = resultSet.getInt("goalGuestTeam");
        LocalDateTime gameDate = resultSet.getTimestamp("gameDate").toLocalDateTime();
        GameStatus status = GameStatus.valueOf(resultSet.getString("status"));
        Float coefficientOnHomeTeam = resultSet.getFloat("coefficientOnHomeTeam");
        Float coefficientOnDraw = resultSet.getFloat("coefficientOnDraw");
        Float coefficientOnGuestTeam = resultSet.getFloat("coefficientOnGuestTeam");
        GameResult result = GameResult.valueOf(resultSet.getString("result"));
        return new Game(idGame, homeTeam, guestTeam, goalHomeTeam, goalGuestTeam, gameDate, status, coefficientOnHomeTeam, coefficientOnDraw, coefficientOnGuestTeam, result);
    }
}
