package ru.arsentiev.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.arsentiev.exception.RepositoryException;
import ru.arsentiev.processing.connection.ConnectionGetter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserExistsRepository {
    private static final Logger logger = LogManager.getLogger();
    private final ConnectionGetter connectionGetter;

    public UserExistsRepository(ConnectionGetter connectionGetter) {
        this.connectionGetter = connectionGetter;
    }

    //language=PostgreSQL
    private static final String SELECT_1_BY_EMAIL = "SELECT 1 FROM users WHERE email = ?;";
    //language=PostgreSQL
    private static final String SELECT_1_BY_NICKNAME = "SELECT 1 FROM users WHERE nickname = ?;";
    //language=PostgreSQL
    private static final String SELECT_1_BY_PHONE_NUMBER = "SELECT 1 FROM users WHERE phonenumber = ?;";

    public boolean existsByEmail(String email) {
        return executeMyQuery(email, SELECT_1_BY_EMAIL);
    }

    public boolean existsByNickname(String nickname) {
        return executeMyQuery(nickname, SELECT_1_BY_NICKNAME);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return executeMyQuery(phoneNumber, SELECT_1_BY_PHONE_NUMBER);
    }

    private boolean executeMyQuery(String what, String select) {
        try (Connection connection = connectionGetter.get();
             PreparedStatement preparedStatement = connection.prepareStatement(select)) {
            preparedStatement.setString(1, what);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException | InterruptedException e) {
            logger.error("Failed to execute query: " + select + ". Error: " + e.getLocalizedMessage());
            throw new RepositoryException(e);
        }
    }
}
