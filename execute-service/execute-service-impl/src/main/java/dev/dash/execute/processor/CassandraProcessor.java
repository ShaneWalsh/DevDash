package dev.dash.execute.processor;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.ColumnDefinitions;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;

import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.security.AuditLogicService;
import net.minidev.json.JSONObject;

public class CassandraProcessor implements DBProcessor {

    @Autowired
    private AuditLogicService auditLogicService;

    private Cluster cluster;

    private Session session;

    @Override
    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig,
            ExecutionData executionData) {

        connect("127.0.0.1", 9142);
        this.session = getSession();
        ResultSet execute = session.execute("");
        for (Row row : execute) {
            String json = row.getString(0);
            ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
            JSONObject obj = new JSONObject();
            // for (int i=1; i<=numColumns; i++) {
            //     String column_name = row.getColumnLabel(i);
            //     obj.put(column_name, rs.getObject(column_name));
            // }
            // json.put(obj);
            // ... do something with JSON string
        }
        return null;
    }

    public void connect(String node, Integer port) {
        Builder b = Cluster.builder().addContactPoint(node);
        if (port != null) {
            b.withPort(port);
        }
        cluster = b.build();

        session = cluster.connect();
    }

    public Session getSession() {
        return this.session;
    }

    public void close() {
        session.close();
        cluster.close();
    }
    
}
