package dev.dash.model.builder;

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
}