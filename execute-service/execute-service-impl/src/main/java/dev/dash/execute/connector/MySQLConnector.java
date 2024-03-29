package dev.dash.execute.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dev.dash.model.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MySQLConnector extends SQLConnector{

    public MySQLConnector(ConnectionConfig connectionConfig) {
        super(connectionConfig);
    }

    public Connection connect() {
        try {
            this.connection = DriverManager.getConnection(connectionConfig.getUrl(), connectionConfig.getUsername(), connectionConfig.getPassword());
        } catch (SQLException e) {
            log.error("Failed to connect to DB : " + connectionConfig.getCode(), e.getCause());
            return null;
        }
        return connection;
    }

}
