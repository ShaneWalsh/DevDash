package dev.dash.model.body;

import java.util.List;
import lombok.Data;

@Data
public class QueryExecution {
    private String elementCode;
    private String queryCode;
    private String exeType;
    private List<SchemaConnection> schemaToConnectionsArray;
    private ExecutionData exeData;
}