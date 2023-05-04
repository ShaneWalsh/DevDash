package dev.dash.execute.connector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import dev.dash.model.ConnectionConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PostgreSQLConnector extends SQLConnector{
    
    public PostgreSQLConnector(ConnectionConfig connectionConfig) {
        super(connectionConfig);
    }

    public Connection connect() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(connectionConfig.getUrl(), connectionConfig.getUsername(), connectionConfig.getPassword());
        } catch (SQLException e) {
            log.error("Failed to connect to DB : " + connectionConfig.getCode(), e.getCause());
            return null;
        } catch (ClassNotFoundException e) {
            log.error("Failed to connect to DB : Class not found org.postgresql.Driver");
            e.printStackTrace();
        }
        return connection;
    }
}
