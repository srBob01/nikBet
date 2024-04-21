package ru.arsentiev.repository;

import ru.arsentiev.dto.user.controller.UserMoneyControllerDto;
import ru.arsentiev.entity.User;
import ru.arsentiev.entity.UserRole;
import ru.arsentiev.exception.DaoException;
import ru.arsentiev.singleton.connection.ConnectionManager;
import ru.arsentiev.singleton.query.UserQueryCreator;
import ru.arsentiev.singleton.query.entity.UpdatedUserFields;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDao implements BaseDao<Long, User> {
    private final ConnectionManager connectionManager;
    private final UserQueryCreator userQueryCreator;

    public UserDao(ConnectionManager connectionManager, UserQueryCreator userQueryCreator) {
        this.connectionManager = connectionManager;
        this.userQueryCreator = userQueryCreator;
    }

    //language=PostgreSQL
    private static final String INSERT_USER = "INSERT INTO users" +
                                              "(nickname, firstName, lastName, patronymic, password, phoneNumber, email, birthDate)" +
                                              "VALUES (?, ?, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'));";
    //language=PostgreSQL
    private static final String UPDATE_USER = "UPDATE users SET " +
                                              "nickname = ?, " +
                                              "email = ?, " +
                                              "firstName = ?, " +
                                              "lastName = ?, " +
                                              "patronymic = ?, " +
                                              "phoneNumber = ?, " +
                                              "birthDate = TO_DATE(?, 'YYYY-MM-DD') " +
                                              "WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String DEPOSIT_BALANCE_USER = "UPDATE users SET " +
                                                       "accountBalance = accountBalance + ? " +
                                                       "WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String WITHDRAW_BALANCE_USER = "UPDATE users SET " +
                                                        "accountBalance = accountBalance - ? " +
                                                        "WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String UPDATE_PAS_USER = "UPDATE users SET password = ? WHERE email = ?;";
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
    private static final String SELECT_PASSWORD_USER = "SELECT password FROM users WHERE email = ?";
    //language=PostgreSQL
    private static final String SELECT_BALANCE_USER = "SELECT accountBalance FROM users WHERE iduser = ?";
    //language=PostgreSQL
    private static final String SELECT_USER_BY_LOGIN = "SELECT idUser, nickname, firstName, lastName, patronymic," +
                                                       " password, phoneNumber, email, birthDate, accountBalance, role" +
                                                       " FROM users WHERE email = ?;";

    @Override
    public User insert(User user) { //INSERT_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getNickname());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            if (user.getPatronymic().isEmpty()) {
                preparedStatement.setNull(4, Types.VARCHAR);
            } else {
                preparedStatement.setString(4, user.getPatronymic());
            }
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setString(6, user.getPhoneNumber());
            preparedStatement.setString(7, user.getEmail());
            preparedStatement.setDate(8, Date.valueOf(user.getBirthDate()));

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

    public User selectByLogin(String login) { //SELECT_USER_BY_LOGIN
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_LOGIN)) {
            preparedStatement.setString(1, login);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserWithoutPassword(rs);
                }
            }
            return null;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public Optional<String> selectPasswordByLogin(String login) { //SELECT_PASSWORD_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PASSWORD_USER)) {
            String result = null;
            preparedStatement.setString(1, login);
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
            preparedStatement.setLong(1, idUser);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getBigDecimal("accountBalance"));
                }
            }
            return Optional.empty();
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

            preparedStatement.setString(1, user.getNickname());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getPatronymic());
            preparedStatement.setString(6, user.getPhoneNumber());
            preparedStatement.setDate(7, Date.valueOf(user.getBirthDate()));
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public void updateDescriptionWithDynamicCreation(User user, UpdatedUserFields fields) {
        Optional<String> sql = userQueryCreator.createUserUpdateQuery(user, fields);
        if (sql.isEmpty()) {
            return;
        }
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.get())) {

            preparedStatement.executeUpdate();
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public void depositMoneyById(UserMoneyControllerDto userMoneyControllerDto) { //UPDATE_BALANCE_USER
        actionMoneyById(userMoneyControllerDto, DEPOSIT_BALANCE_USER);
    }

    public void withdrawMoneyById(UserMoneyControllerDto userMoneyControllerDto) { //UPDATE_BALANCE_USER
        actionMoneyById(userMoneyControllerDto, WITHDRAW_BALANCE_USER);
    }

    private void actionMoneyById(UserMoneyControllerDto userMoneyControllerDto, String action) {
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(action)) {

            preparedStatement.setBigDecimal(1, userMoneyControllerDto.summa());
            preparedStatement.setLong(2, userMoneyControllerDto.idUser());

            preparedStatement.executeUpdate();
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    public void updatePasswordByLogin(User user) { //UPDATE_PAS_USER
        try (Connection connection = connectionManager.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PAS_USER)) {

            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getEmail());

            preparedStatement.executeUpdate();
        } catch (SQLException | InterruptedException e) {
            throw new DaoException(e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String password = rs.getString("password");
        User user = mapResultSetToUserWithoutPassword(rs);
        user.setPassword(password);
        return user;
    }

    private User mapResultSetToUserWithoutPassword(ResultSet rs) throws SQLException {
        long idUser = rs.getLong("idUser");
        String nickname = rs.getString("nickname");
        String firstName = rs.getString("firstName");
        String lastName = rs.getString("lastName");
        String patronymic = rs.getString("patronymic");
        String phoneNumber = rs.getString("phoneNumber");
        String email = rs.getString("email");
        LocalDate birthDate = rs.getDate("birthDate").toLocalDate();
        BigDecimal accountBalance = rs.getBigDecimal("accountBalance");
        String roleString = rs.getString("role");
        UserRole role = UserRole.valueOf(roleString);

        return User.builder()
                .idUser(idUser)
                .nickname(nickname)
                .firstName(firstName)
                .lastName(lastName)
                .patronymic(patronymic)
                .phoneNumber(phoneNumber)
                .email(email)
                .birthDate(birthDate)
                .accountBalance(accountBalance)
                .role(role)
                .build();
    }
}
