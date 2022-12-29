package dev.dash.execute.processor;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.ColumnDefinitions;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.dash.execute.connector.CassandraConnector;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.security.AuditLogicService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;

@Slf4j
@Service
public class CassandraProcessor implements ResourceProcessor {

    @Autowired
    private AuditLogicService auditLogicService;

    @Override
    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig,
            ExecutionData executionData) {
        
        CassandraConnector cass = new CassandraConnector(connectionConfig);
        Session session = cass.connect();
        // select or CUD
        ResultSet execute = session.execute(queryConfig.getQueryString());

        JSONArray json = jsonify(execute);
        cass.close();
        return json;
    }

    private JSONArray jsonify(ResultSet execute) {
        JSONArray json = new JSONArray();
        for (Row row : execute) {
            ColumnDefinitions columnDefinitions = row.getColumnDefinitions();
            JSONObject obj = new JSONObject();
            int numColumns = columnDefinitions.size();
            for (int i=0; i < numColumns; i++) {
                 String column_name = columnDefinitions.getName(i);
                 obj.put(column_name, row.getObject(column_name));
            }
            json.put(obj);
        }
        return json;
    }

    
}
