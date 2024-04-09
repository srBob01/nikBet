package ru.arsentiev.repository;

import ru.arsentiev.entity.Game;
import ru.arsentiev.entity.GameResult;
import ru.arsentiev.entity.Prediction;
import ru.arsentiev.entity.User;
import ru.arsentiev.exception.DaoException;
import ru.arsentiev.utils.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PredictionDao implements BaseDao<Long, Prediction> {
    private final ConnectionManager connectionManager;
    private final GameDao gameDAO;
    private final UserDao userDAO;
    public PredictionDao(ConnectionManager connectionManager, GameDao gameDAO, UserDao userDAO) {
        this.connectionManager = connectionManager;
        this.gameDAO = gameDAO;
        this.userDAO = userDAO;
    }
    
    //language=PostgreSQL
    private static final String INSERT_PREDICTION = "INSERT INTO predictions (idGame, idUser, predictionDate, summa," +
            " prediction) VALUES (?, ?, ?, ?, ?);";
    //language=PostgreSQL
    private static final String SELECT_ALL_PREDICTIONS = "SELECT idprediction, idgame, iduser, predictiondate, summa," +
            " prediction FROM predictions;";
    //language=PostgreSQL
    private static final String SELECT_PREDICTION_BY_ID = "SELECT idprediction, idgame, iduser, predictiondate," +
            " summa, prediction FROM predictions WHERE idPrediction = ?;";
    //language=PostgreSQL
    private static final String SELECT_PREDICTION_BY_USER_ID = "SELECT idprediction, idgame, iduser, predictiondate," +
            " summa, prediction FROM predictions WHERE iduser = ?;";
    //language=PostgreSQL
    private static final String SELECT_PREDICTIONS_BY_GAME_ID = "SELECT idprediction, idgame, iduser, predictiondate," +
            " summa, prediction FROM predictions WHERE idgame = ?;";
    //language=PostgreSQL
    private static final String DELETE_PREDICTION = "DELETE FROM predictions WHERE idPrediction = ?;";
    //language=PostgreSQL
    private static final String UPDATE_PREDICTION = "UPDATE predictions SET idGame = ?, idUser = ?, predictionDate = ?, summa = ?, prediction = ? WHERE idPrediction = ?;";

    @Override
    public Prediction insert(Prediction prediction) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PREDICTION, Statement.RETURN_GENERATED_KEYS)) {

            setStatement(prediction, preparedStatement);

            preparedStatement.executeUpdate();

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                prediction.setIdPrediction(generatedKeys.getLong("idPrediction"));
            }
            return prediction;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public List<Prediction> selectAll() {
        List<Prediction> predictions = new ArrayList<>();
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PREDICTIONS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                predictions.add(extractPredictionFromResultSet(resultSet));
            }
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
        return predictions;
    }

    @Override
    public Optional<Prediction> selectById(Long id) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PREDICTION_BY_ID)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractPredictionFromResultSet(resultSet));
                }
            }
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
        return Optional.empty();
    }

    public List<Prediction> selectByUserId(Long userId) {
        return selectByOtherId(userId, SELECT_PREDICTION_BY_USER_ID);
    }

    public List<Prediction> selectByGameId(Long gameId) {
        return selectByOtherId(gameId, SELECT_PREDICTIONS_BY_GAME_ID);
    }

    private List<Prediction> selectByOtherId(Long Id, String selectPredictionsById) {
        List<Prediction> predictions = new ArrayList<>();
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(selectPredictionsById)) {

            preparedStatement.setLong(1, Id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    predictions.add(extractPredictionFromResultSet(resultSet));
                }
            }
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
        return predictions;
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PREDICTION)) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean update(Prediction prediction) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PREDICTION)) {

            setStatement(prediction, preparedStatement);
            preparedStatement.setLong(6, prediction.getIdPrediction());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }


    private void setStatement(Prediction prediction, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(1, prediction.getGame().getIdGame());
        preparedStatement.setLong(2, prediction.getUser().getIdUser());
        preparedStatement.setTimestamp(3, Timestamp.valueOf(prediction.getPredictionDate()));
        preparedStatement.setBigDecimal(4, prediction.getSumma());
        preparedStatement.setString(5, prediction.getPrediction().name());
    }

    private Prediction extractPredictionFromResultSet(ResultSet resultSet) throws SQLException {
        Long idPrediction = resultSet.getLong("idPrediction");
        LocalDateTime predictionDate = resultSet.getTimestamp("predictionDate").toLocalDateTime();
        BigDecimal summa = resultSet.getBigDecimal("summa");
        String prediction = resultSet.getString("prediction");

        Game game = gameDAO.selectById(resultSet.getLong("idGame"),
                resultSet.getStatement().getConnection()).orElseThrow(() -> new SQLException("Game not found"));
        User user = userDAO.selectById(resultSet.getLong("idUser"),
                resultSet.getStatement().getConnection()).orElseThrow(() -> new SQLException("User not found"));

        return new Prediction(idPrediction, game, user, predictionDate, summa, GameResult.valueOf(prediction));
    }
}
