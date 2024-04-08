package ru.arsentiev.utils;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static BlockingQueue<Connection> pool;

    static {
        initConnectionPool();
    }

    private static void initConnectionPool() {
        String poolSize = PropertyUtil.get("db.pool.size");
        int size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);
        for (int i = 0; i < size; i++) {
            Connection connection = open();
            var proxyConnection = (Connection) Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    ((proxy, method, args) -> method.getName().equals("close") ?
                            pool.add((Connection) proxy) :
                            method.invoke(connection, args)));
            pool.add(proxyConnection);
        }

    }

    public Connection get() throws InterruptedException {
        return pool.take();
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertyUtil.get(URL_KEY),
                    PropertyUtil.get(USERNAME_KEY),
                    PropertyUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
