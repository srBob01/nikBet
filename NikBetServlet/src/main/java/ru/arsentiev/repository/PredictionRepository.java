package ru.arsentiev.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.arsentiev.entity.*;
import ru.arsentiev.exception.RepositoryException;
import ru.arsentiev.processing.connection.ConnectionGetter;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PredictionRepository implements BaseRepository<Long, Prediction> {
    private static final Logger logger = LogManager.getLogger();
    private final ConnectionGetter connectionGetter;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    public PredictionRepository(ConnectionGetter connectionGetter, GameRepository gameRepository, UserRepository userRepository) {
        this.connectionGetter = connectionGetter;
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    //language=PostgreSQL
    private static final String INSERT_PREDICTION = "INSERT INTO predictions (idGame, idUser, summa," +
                                                    " prediction, coefficient) VALUES (?, ?, ?, ?, ?);";
    //language=PostgreSQL
    private static final String SELECT_ALL_PREDICTIONS = "SELECT idprediction, idgame, iduser, predictiondate, summa," +
                                                         " predictionstatus, prediction, coefficient FROM predictions;";
    //language=PostgreSQL
    private static final String SELECT_PREDICTION_BY_ID = "SELECT idprediction, idgame, iduser, predictiondate," +
                                                          " summa, predictionstatus, prediction, coefficient FROM predictions WHERE idPrediction = ?;";
    //language=PostgreSQL
    private static final String SELECT_BET_NOT_PLAYED_PREDICTION_BY_USER_ID_LIMIT = "SELECT idprediction, idgame, iduser, predictiondate," +
                                                                                    " summa, predictionstatus, prediction, coefficient FROM predictions" +
                                                                                    " WHERE iduser = ? AND predictionstatus = 'BetNotPlayed' LIMIT 3;";
    //language=PostgreSQL
    private static final String SELECT_BET_PLAYED_PREDICTION_BY_USER_ID_LIMIT = "SELECT idprediction, idgame, iduser, predictiondate," +
                                                                                " summa, predictionstatus, prediction, coefficient FROM predictions" +
                                                                                " WHERE iduser = ? AND predictionstatus = 'BetPlayed' LIMIT 3;";
    //language=PostgreSQL
    private static final String SELECT_BET_NOT_PLAYED_PREDICTION_BY_USER_ID = "SELECT idprediction, idgame, iduser, predictiondate," +
                                                                              " summa, predictionstatus, prediction, coefficient FROM predictions" +
                                                                              " WHERE iduser = ? AND predictionstatus = 'BetNotPlayed';";
    //language=PostgreSQL
    private static final String SELECT_BET_PLAYED_PREDICTION_BY_USER_ID = "SELECT idprediction, idgame, iduser, predictiondate," +
                                                                          " summa, predictionstatus, prediction, coefficient FROM predictions" +
                                                                          " WHERE iduser = ? AND predictionstatus = 'BetPlayed';";
    //language=PostgreSQL
    private static final String SELECT_PREDICTIONS_BY_GAME_ID = "SELECT idprediction, idgame, iduser, predictiondate," +
                                                                " summa, predictionstatus, prediction, coefficient FROM predictions WHERE idgame = ?;";
    //language=PostgreSQL
    private static final String DELETE_PREDICTION = "DELETE FROM predictions WHERE idPrediction = ?;";
    //language=PostgreSQL
    private static final String UPDATE_PREDICTION = "UPDATE predictions SET idGame = ?, idUser = ?, predictionDate = ?, summa = ?, prediction = ?," +
                                                    " predictionstatus = ?, coefficient = ? WHERE idPrediction = ?;";

    //language=PostgreSQL
    private static final String UPDATE_PREDICTION_STATUS = "UPDATE predictions SET predictionstatus = 'BetPlayed' WHERE idPrediction = ?;";


    @Override
    public boolean insert(Prediction prediction) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_PREDICTION, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, prediction.getGame().getIdGame());
            preparedStatement.setLong(2, prediction.getUser().getIdUser());
            preparedStatement.setBigDecimal(3, prediction.getSumma());
            preparedStatement.setString(4, prediction.getPrediction().name());
            preparedStatement.setFloat(5, prediction.getCoefficient());

            boolean res = preparedStatement.executeUpdate() > 0;

            var generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                prediction.setIdPrediction(generatedKeys.getLong("idPrediction"));
                Timestamp timestamp = generatedKeys.getTimestamp("predictionDate");
                prediction.setPredictionDate(timestamp.toLocalDateTime());
            }
            return res;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to insert prediction: " + prediction.toString() + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<Prediction> selectAll() {
        List<Prediction> predictions = new ArrayList<>();
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_PREDICTIONS);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                predictions.add(extractPredictionFromResultSet(resultSet));
            }
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select predictions. Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
        return predictions;
    }

    @Override
    public Optional<Prediction> selectById(Long id) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PREDICTION_BY_ID)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(extractPredictionFromResultSet(resultSet));
                }
            }
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select prediction by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
        return Optional.empty();
    }

    public List<Prediction> selectByUserIdLimitBetNotPlayed(Long userId) {
        return selectByOtherId(userId, SELECT_BET_NOT_PLAYED_PREDICTION_BY_USER_ID_LIMIT);
    }

    public List<Prediction> selectByUserIdLimitBetPlayed(Long userId) {
        return selectByOtherId(userId, SELECT_BET_PLAYED_PREDICTION_BY_USER_ID_LIMIT);
    }

    public List<Prediction> selectByUserIdBetNotPlayed(Long userId) {
        return selectByOtherId(userId, SELECT_BET_NOT_PLAYED_PREDICTION_BY_USER_ID);
    }

    public List<Prediction> selectByUserIdBetPlayed(Long userId) {
        return selectByOtherId(userId, SELECT_BET_PLAYED_PREDICTION_BY_USER_ID);
    }

    public List<Prediction> selectByGameId(Long gameId) {
        return selectByOtherId(gameId, SELECT_PREDICTIONS_BY_GAME_ID);
    }

    private List<Prediction> selectByOtherId(Long id, String selectPredictionsById) {
        List<Prediction> predictions = new ArrayList<>();
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(selectPredictionsById)) {

            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    predictions.add(extractPredictionFromResultSet(resultSet));
                }
            }
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select prediction by other id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
        return predictions;
    }

    @Override
    public boolean delete(Long id) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_PREDICTION)) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to delete prediction with id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean update(Prediction prediction) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PREDICTION)) {

            preparedStatement.setLong(1, prediction.getGame().getIdGame());
            preparedStatement.setLong(2, prediction.getUser().getIdUser());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(prediction.getPredictionDate()));
            preparedStatement.setBigDecimal(4, prediction.getSumma());
            preparedStatement.setString(5, prediction.getPrediction().name());
            preparedStatement.setString(6, prediction.getPredictionStatus().name());
            preparedStatement.setFloat(7, prediction.getCoefficient());
            preparedStatement.setLong(8, prediction.getIdPrediction());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to update prediction : " + (prediction != null ? prediction.toString() : "null") + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public void updatePredictionStatusOfList(List<Long> listIdPrediction) {
        try (Connection connection = connectionGetter.get()) {
            listIdPrediction.forEach(id -> updatePredictionStatus(id, connection));
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to update predictions status of list(ids) : " + listIdPrediction.toString() + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    private void updatePredictionStatus(Long idPrediction, Connection connection) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PREDICTION_STATUS)) {

            preparedStatement.setLong(1, idPrediction);

            preparedStatement.executeUpdate();
        } catch (SQLException | NullPointerException e) {
            logger.error("Failed to update prediction status with id : " + idPrediction + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    private Prediction extractPredictionFromResultSet(ResultSet resultSet) throws SQLException {
        long idPrediction = resultSet.getLong("idPrediction");
        LocalDateTime predictionDate = resultSet.getTimestamp("predictionDate").toLocalDateTime();
        BigDecimal summa = resultSet.getBigDecimal("summa");
        String prediction = resultSet.getString("prediction");

        Game game = gameRepository.selectById(resultSet.getLong("idGame"),
                resultSet.getStatement().getConnection()).orElseThrow(() -> new SQLException("Game not found"));
        User user = userRepository.selectById(resultSet.getLong("idUser"),
                resultSet.getStatement().getConnection()).orElseThrow(() -> new SQLException("User not found"));

        float coefficient = resultSet.getBigDecimal("coefficient").floatValue();

        String predictionStatus = resultSet.getString("predictionStatus");
        return Prediction.builder()
                .idPrediction(idPrediction)
                .predictionDate(predictionDate)
                .game(game)
                .user(user)
                .summa(summa)
                .prediction(GameResult.valueOf(prediction))
                .predictionStatus(PredictionStatus.valueOf(predictionStatus))
                .coefficient(coefficient)
                .build();
    }
}
