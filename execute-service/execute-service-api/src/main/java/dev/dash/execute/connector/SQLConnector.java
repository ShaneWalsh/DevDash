package dev.dash.execute.connector;

import java.sql.Connection;
import java.sql.SQLException;

import dev.dash.model.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SQLConnector {

    protected Connection connection;

    protected ConnectionConfig connectionConfig;

    public ConnectionConfig getConnectionConfig() {
        return connectionConfig;
    }

    public SQLConnector(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public abstract Connection connect();

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Failed to close connection to DB : " + getConnectionConfig().getCode(), e.getCause());
        }
    }
}
