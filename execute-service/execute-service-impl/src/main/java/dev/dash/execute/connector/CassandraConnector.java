package dev.dash.execute.connector;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Cluster.Builder;

import dev.dash.model.ConnectionConfig;

public class CassandraConnector {
    
    private ConnectionConfig connectionConfig;

    private Cluster cluster;

    private Session session;

    public CassandraConnector(ConnectionConfig connectionConfig) {
        this.connectionConfig = connectionConfig;
    }

    public Session connect() {
        String[] split = connectionConfig.getUrl().split(":");
        String node = split[0];
        Integer port = Integer.parseInt(split[1]);

        Builder b = Cluster.builder().addContactPoint(node);
        if (port != null) {
            b.withPort(port);
        }
        if(connectionConfig.getUsername() != null && connectionConfig.getPassword() != null){
            b.withCredentials(connectionConfig.getUsername(), connectionConfig.getPassword());
        }
        cluster = b.build();

        session = cluster.connect();
        return session;
    }

    public Session getSession() {
        return this.session;
    }

    public void close() {
        session.close();
        cluster.close();
    }

}
