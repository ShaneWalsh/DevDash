package dev.dash.execute;

import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecuteResponse;
import dev.dash.model.body.ExecutionData;
import dev.dash.model.body.QueryExecution;

public interface QueryExecutorService {
 
	public ExecuteResponse processQuery(QueryExecution queryExecution);

    public ExecuteResponse processQuery(String queryCode, String connectionCode,ExecutionData executionData);

    /**
     * Execute a query and return the result in json format
     * 
     * @param executionData
     * 
     * @param queryCode     TODO add in a map here for the different panels and
     *                      their values, which can be used in the query.
     */
    public ExecuteResponse processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData);

}