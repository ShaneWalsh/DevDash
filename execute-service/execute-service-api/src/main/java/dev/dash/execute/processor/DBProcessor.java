package dev.dash.execute.processor;

import org.json.JSONArray;

import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;

public interface DBProcessor {

    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData);

}
