package ru.arsentiev.datalayer;

import ru.arsentiev.processing.connection.ConnectionGetter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class TestConnectionGetter implements ConnectionGetter {
    private static final TestConnectionGetter INSTANCE = new TestConnectionGetter();

    public static TestConnectionGetter getInstance() {
        return INSTANCE;
    }

    private final String url;
    private final String user;
    private final String password;

    private TestConnectionGetter() {
        Properties properties = new Properties();
        try {
            properties.load(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("test.properties"));
            Class.forName(properties.getProperty("db.driver"));
            url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            password = properties.getProperty("db.password");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Connection get() throws SQLException, InterruptedException {
        return DriverManager.getConnection(url, user, password);
    }
}
