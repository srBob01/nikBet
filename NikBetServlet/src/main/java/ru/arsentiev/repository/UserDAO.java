package ru.arsentiev.repository;

import ru.arsentiev.entity.User;
import ru.arsentiev.entity.UserRole;
import ru.arsentiev.exception.DaoException;
import ru.arsentiev.utils.ConnectionManager;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO implements BaseDAO<Long, User> {
    private final ConnectionManager connectionManager;
    private UserDAO(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }
    //language=PostgreSQL
    private static final String INSERT_USER = "INSERT INTO users" +
            "(nickname, firstName, lastName, patronymic, password, phoneNumber, email, birthDate, accountBalance, role)" +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    //language=PostgreSQL
    private static final String UPDATE_USER = "UPDATE users SET " +
            "email = ?, " +
            "firstName = ?, " +
            "lastName = ?, " +
            "patronymic = ?, " +
            "phoneNumber = ?, " +
            "birthDate = ?, " +
            "accountBalance = ?, " +
            "password = ? " +
            "WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String UPDATE_DESCRIPTION_USER = "UPDATE users SET " +
            "nickname = ?, " +
            "firstName = ?, " +
            "lastName = ?, " +
            "patronymic = ?, " +
            "phoneNumber = ?, " +
            "birthDate = ? " +
            "WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String UPDATE_BALANCE_USER = "UPDATE users SET " +
            "accountBalance = ? " +
            "WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String UPDATE_PAS_USER = "UPDATE users SET password = ? WHERE iduser = ?;";
    //language=PostgreSQL
    private static final String DELETE_USER = "DELETE FROM users WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String SELECT_USER_BY_ID = "SELECT idUser, nickname, firstName, lastName, patronymic," +
            " password, phoneNumber, email, birthDate, accountBalance, role" +
            " FROM users WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String SELECT_ALL_USERS = "SELECT iduser, nickname, firstname, lastname, patronymic," +
            " password, phonenumber, email, birthdate, accountbalance, role" +
            " FROM users ORDER BY iduser;";
    //language=PostgreSQL
    private static final String SELECT_PASSWORD_USER = "SELECT password FROM users WHERE nickname = ?";
    //language=PostgreSQL
    private static final String SELECT_BALANCE_USER = "SELECT accountBalance FROM users WHERE iduser = ?";

    @Override
    public User insert(User user) { //INSERT_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getNickname());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getPatronymic());
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setString(6, user.getPhoneNumber());
            preparedStatement.setString(7, user.getEmail());
            preparedStatement.setDate(8, Date.valueOf(user.getBirthDate()));
            preparedStatement.setBigDecimal(9, user.getAccountBalance());
            preparedStatement.setString(10, user.getRole().name());

            preparedStatement.executeUpdate();

            var keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                user.setIdUser(keys.getLong("idUser"));
            }
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
        return user;
    }

    @Override
    public List<User> selectAll() { //SELECT_ALL_USERS
        List<User> users = new ArrayList<>();
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
        return users;
    }

    @Override
    public Optional<User> selectById(Long id) { //SELECT_INFO_USER_BY_ID
        try (Connection connection = connectionManager.get()) {
            return selectById(id, connection);
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public Optional<User> selectById(Long id, Connection connection) { //SELECT_INFO_USER_BY_ID
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            User user = null;
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<String> selectPasswordByNickname(String nickname) { //SELECT_PASSWORD_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PASSWORD_USER)) {
            String result = null;
            preparedStatement.setString(1, nickname);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString("password");
                }
            }
            return Optional.ofNullable(result);
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public Optional<BigDecimal> selectBalanceById(Long idUser) { //SELECT_BALANCE_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BALANCE_USER)) {
            BigDecimal result = null;
            preparedStatement.setLong(1, idUser);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    result = rs.getBigDecimal("accountBalance");
                }
            }
            return Optional.ofNullable(result);
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public boolean delete(Long id) { //DELETE_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }

    }

    @Override
    public boolean update(User user) { //UPDATE_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)) {

            preparedStatement.setString(1, user.getEmail());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            preparedStatement.setString(4, user.getPatronymic());
            preparedStatement.setString(5, user.getPhoneNumber());
            preparedStatement.setDate(6, Date.valueOf(user.getBirthDate()));
            preparedStatement.setBigDecimal(7, user.getAccountBalance());
            preparedStatement.setString(8, user.getPassword());
            preparedStatement.setLong(9, user.getIdUser());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public boolean updateDescriptionBiId(Long idUser, User newUser) { //UPDATE_DESCRIPTION_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_DESCRIPTION_USER)) {

            preparedStatement.setString(1, newUser.getFirstName());
            preparedStatement.setString(2, newUser.getLastName());
            preparedStatement.setString(3, newUser.getPatronymic());
            preparedStatement.setString(4, newUser.getPhoneNumber());
            preparedStatement.setDate(5, Date.valueOf(newUser.getBirthDate()));
            preparedStatement.setLong(6, idUser);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public boolean updateBalanceById(Integer idUser, BigDecimal balance) { //UPDATE_BALANCE_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BALANCE_USER)) {

            preparedStatement.setBigDecimal(1, balance);
            preparedStatement.setInt(2, idUser);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public boolean updatePasswordById(Long idUser, String newPassword) { //UPDATE_PAS_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PAS_USER)) {

            preparedStatement.setString(1, newPassword);
            preparedStatement.setLong(2, idUser);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        long idUser = rs.getLong("idUser");
        String nickname = rs.getString("nickname");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String patronymic = rs.getString("patronymic");
        String password = rs.getString("password");
        String phoneNumber = rs.getString("phoneNumber");
        String email = rs.getString("email");
        LocalDate birthDate = rs.getDate("birthDate").toLocalDate();
        BigDecimal accountBalance = rs.getBigDecimal("accountBalance");
        String roleString = rs.getString("role");
        UserRole role = UserRole.valueOf(roleString);
        return new User(idUser, nickname, firstName, lastName, patronymic,
                password, phoneNumber, email, birthDate, accountBalance, role, null);
    }
}
