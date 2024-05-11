package ru.arsentiev.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.arsentiev.entity.User;
import ru.arsentiev.entity.UserRole;
import ru.arsentiev.exception.RepositoryException;
import ru.arsentiev.processing.connection.ConnectionGetter;
import ru.arsentiev.processing.query.UserQueryCreator;
import ru.arsentiev.processing.query.entity.UpdatedUserFields;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements BaseRepository<Long, User> {
    private static final Logger logger = LogManager.getLogger();
    private final ConnectionGetter myConnectionGetter;
    private final UserQueryCreator userQueryCreator;

    public UserRepository(ConnectionGetter connectionGetter, UserQueryCreator userQueryCreator) {
        this.myConnectionGetter = connectionGetter;
        this.userQueryCreator = userQueryCreator;
    }

    //language=PostgreSQL
    private static final String INSERT_USER = "INSERT INTO users" +
                                              "(nickname, firstName, lastName, patronymic, password, phoneNumber," +
                                              " email, birthDate, salt)" +
                                              "VALUES (?, ?, ?, ?, ?, ?, ?, TO_DATE(?, 'YYYY-MM-DD'), ?);";
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
    private static final String UPDATE_PAS_USER = "UPDATE users SET password = ?, salt = ? WHERE email = ?;";
    //language=PostgreSQL
    private static final String DELETE_USER = "DELETE FROM users WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String SELECT_USER_BY_ID = "SELECT idUser, nickname, firstName, lastName, patronymic," +
                                                    " password, phoneNumber, email, birthDate, accountBalance, role, salt" +
                                                    " FROM users WHERE idUser = ?;";
    //language=PostgreSQL
    private static final String SELECT_ALL_USERS = "SELECT iduser, nickname, firstname, lastname, patronymic," +
                                                   " password, phonenumber, email, birthdate, accountbalance, role, salt" +
                                                   " FROM users WHERE role = 'USER' ORDER BY iduser;";
    //language=PostgreSQL
    private static final String SELECT_PASSWORD_SALT_USER = "SELECT password, salt FROM users WHERE email = ?";
    //language=PostgreSQL
    private static final String SELECT_BALANCE_USER = "SELECT accountBalance FROM users WHERE iduser = ?";
    //language=PostgreSQL
    private static final String SELECT_USER_BY_LOGIN = "SELECT idUser, nickname, firstName, lastName, patronymic," +
                                                       " password, phoneNumber, email, birthDate, accountBalance, role, salt" +
                                                       " FROM users WHERE email = ?;";
    //language=PostgreSQL
    private static final String SELECT_USER_BY_NICKNAME = "SELECT idUser, nickname, firstName, lastName, patronymic," +
                                                          " password, phoneNumber, email, birthDate, accountBalance, role, salt" +
                                                          " FROM users WHERE role = 'USER' AND nickname = ?;";

    @Override
    public boolean insert(User user) { //INSERT_USER
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getNickname());
            preparedStatement.setString(2, user.getFirstName());
            preparedStatement.setString(3, user.getLastName());
            if (user.getPatronymic() == null || user.getPatronymic().isEmpty()) {
                preparedStatement.setNull(4, Types.VARCHAR);
            } else {
                preparedStatement.setString(4, user.getPatronymic());
            }
            preparedStatement.setString(5, user.getPassword());
            preparedStatement.setString(6, user.getPhoneNumber());
            preparedStatement.setString(7, user.getEmail());
            preparedStatement.setDate(8, Date.valueOf(user.getBirthDate()));
            preparedStatement.setString(9, user.getSalt());

            boolean res = preparedStatement.executeUpdate() > 0;

            var keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                user.setIdUser(keys.getLong("idUser"));
            }
            return res;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to insert user: " + user.toString() + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public List<User> selectAll() { //SELECT_ALL_USERS
        List<User> users = new ArrayList<>();
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERS);
             ResultSet rs = preparedStatement.executeQuery()) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select users. Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
        return users;
    }

    @Override
    public Optional<User> selectById(Long id) { //SELECT_INFO_USER_BY_ID
        try (Connection connection = myConnectionGetter.get()) {
            return selectById(id, connection);
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select user by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public Optional<User> selectById(Long id, Connection connection) { //SELECT_INFO_USER_BY_ID
        try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            User user = null;
            preparedStatement.setLong(1, id);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUserWithoutPassword(rs);
                }
            }
            return Optional.ofNullable(user);
        } catch (SQLException | NullPointerException e) {
            logger.error("Failed to select user by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public User selectByLogin(String login) { //SELECT_USER_BY_LOGIN
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_LOGIN)) {
            preparedStatement.setString(1, login);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUserWithoutPassword(rs);
                }
            }
            return null;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select user by login: " + login + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public Optional<User> selectByNickname(String nickname) { //SELECT_USER_BY_NICKNAME
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_NICKNAME)) {
            preparedStatement.setString(1, nickname);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUserWithoutPassword(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select user by nickname: " + nickname + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public User selectPasswordByLogin(String login) { //SELECT_PASSWORD_USER
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_PASSWORD_SALT_USER)) {
            String password = null;
            String salt = null;
            preparedStatement.setString(1, login);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    password = rs.getString("password");
                    salt = rs.getString("salt");
                }
            }
            return User.builder()
                    .salt(salt)
                    .password(password)
                    .build();
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select user by login: " + login + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public Optional<BigDecimal> selectBalanceById(Long idUser) { //SELECT_BALANCE_USER
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BALANCE_USER)) {
            preparedStatement.setLong(1, idUser);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getBigDecimal("accountBalance"));
                }
            }
            return Optional.empty();
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to select user by id: " + idUser + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean delete(Long id) { //DELETE_USER
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to delete user by id: " + id + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean update(User user) { //UPDATE_USER
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)) {

            preparedStatement.setString(1, user.getNickname());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getPatronymic());
            preparedStatement.setString(6, user.getPhoneNumber());
            preparedStatement.setDate(7, Date.valueOf(user.getBirthDate()));
            preparedStatement.setLong(8, user.getIdUser());
            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to update user : " + user.toString() + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public boolean updateDescriptionWithDynamicCreation(User user, UpdatedUserFields fields) {
        String sql = userQueryCreator.createUserUpdateQuery(user, fields);
        if (!sql.equals("empty")) {
            try (Connection connection = myConnectionGetter.get();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                return preparedStatement.executeUpdate() > 0;
            } catch (SQLException | InterruptedException | NullPointerException e) {
                logger.error("Failed to update user : " + user.toString() + ". Error: " + e.getLocalizedMessage());
                throw new RepositoryException(e);
            }
        }
        return true;
    }

    public boolean depositMoneyById(long idUser, BigDecimal summa) { //UPDATE_BALANCE_USER
        return actionMoneyById(idUser, summa, DEPOSIT_BALANCE_USER);
    }

    public boolean withdrawMoneyById(long idUser, BigDecimal summa) { //UPDATE_BALANCE_USER
        return actionMoneyById(idUser, summa, WITHDRAW_BALANCE_USER);
    }

    private boolean actionMoneyById(long idUser, BigDecimal summa, String action) {
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(action)) {

            preparedStatement.setBigDecimal(1, summa);
            preparedStatement.setLong(2, idUser);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to action with money user with id: " + idUser + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    public boolean updatePasswordByLogin(User user) { //UPDATE_PAS_USER
        try (Connection connection = myConnectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PAS_USER)) {

            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setString(2, user.getSalt());
            preparedStatement.setString(3, user.getEmail());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException | InterruptedException | NullPointerException e) {
            logger.error("Failed to update password user: " + user.toString() + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        String password = rs.getString("password");
        String salt = rs.getString("salt");
        User user = mapResultSetToUserWithoutPassword(rs);
        user.setPassword(password);
        user.setSalt(salt);
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
