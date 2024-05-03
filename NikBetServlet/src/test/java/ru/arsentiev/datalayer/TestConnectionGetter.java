package ru.arsentiev.datalayer;

import ru.arsentiev.exception.ConnectionException;
import ru.arsentiev.processing.connection.ConnectionGetter;
import ru.arsentiev.utils.PropertyUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TestConnectionGetter implements ConnectionGetter {

    private static final TestConnectionGetter INSTANCE = new TestConnectionGetter();

    public static TestConnectionGetter getInstance() {
        return INSTANCE;
    }

    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static BlockingQueue<Connection> pool;

    static {
        loadDriver();
        initConnectionPool();
    }

    @Override
    public Connection get() throws InterruptedException {
        return pool.take();
    }

    private static void initConnectionPool() {
        String poolSize = PropertyUtil.getTest("db.pool.size");
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(
                    TestConnectionGetter.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> "close".equals(method.getName()) ? pool.add((Connection) proxy) : method.invoke(connection, args)
            );
            pool.add(proxyConnection);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertyUtil.getTest(URL_KEY),
                    PropertyUtil.getTest(USERNAME_KEY),
                    PropertyUtil.getTest(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new ConnectionException("Failed to open database connection", e);
        }
    }

    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("PostgreSQL JDBC driver not found", e);
        }
    }
}
