package dev.dash.execute.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dev.dash.model.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MySQLConnector {
    
    private Connection connection;

    private ConnectionConfig connectionConfig;

    public MySQLConnector(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public Connection connect() {
        try {
            connection = DriverManager.getConnection(connectionConfig.getUrl(), connectionConfig.getUsername(), connectionConfig.getPassword());
        } catch (SQLException e) {
            log.error("Failed to connect to DB : " + connectionConfig.getCode(), e.getCause());
            return null;
        }
        return connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            log.error("Failed to close connection to DB : " + connectionConfig.getCode(), e.getCause());
        }
    }

    

}
