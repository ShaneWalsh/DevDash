package dev.dash.model.builder;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QueryBuilderData {
    private List<SchemaDTO> schemas;
    private List<QueryDTO> queries;
    private List<ConnectionDTO> connections;

    public QueryBuilderData addSchemaConfig ( SchemaDTO schemaDTO ) {
        if(schemas == null) schemas = new ArrayList<>();
        schemas.add(schemaDTO);
        return this;
    }

    public QueryBuilderData addQueryConfig ( QueryDTO queryDTO ) {
        if(queries == null) queries = new ArrayList<>();
        queries.add(queryDTO);
        return this;
    }

    public QueryBuilderData addConnectionConfig ( ConnectionDTO connectionDTO ) {
        if(connections == null) connections = new ArrayList<>();
        connections.add(connectionDTO);
        return this;
    }
}