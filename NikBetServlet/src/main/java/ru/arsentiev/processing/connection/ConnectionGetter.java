package ru.arsentiev.processing.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionGetter {
    Connection get() throws SQLException, InterruptedException;
}
