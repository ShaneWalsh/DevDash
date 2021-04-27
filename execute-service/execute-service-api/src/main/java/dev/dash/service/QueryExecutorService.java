package dev.dash.service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.model.body.QueryExecution;

import org.json.JSONArray;
import org.json.JSONObject;

public interface QueryExecutorService {
 
	public JSONArray processQuery(QueryExecution queryExecution) throws SQLException;

    public JSONArray processQuery(String queryCode, String connectionCode,ExecutionData executionData) throws SQLException;

    /**
     * Execute a query and return the result in json format
     * 
     * @param executionData
     * 
     * @param queryCode     TODO add in a map here for the different panels and
     *                      their values, which can be used in the query.
     * @throws SQLException
     */
    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData) throws SQLException;

    // https://stackoverflow.com/questions/6514876/most-efficient-conversion-of-resultset-to-json
    public default JSONArray jsonify(ResultSet rs) throws SQLException {
        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rs.getMetaData();
        while(rs.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i=1; i<=numColumns; i++) {
                String column_name = rsmd.getColumnLabel(i);
                obj.put(column_name, rs.getObject(column_name));
            }
            json.put(obj);
        }
        return json;
    }

}