package dev.dash.model.body;

import java.util.List;

import org.json.JSONArray;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple Response wrapper to include more information about the datasource for the UI to parse it properly.
 */
@Data
@AllArgsConstructor
public class ExecuteResponse {
    private List<Object> data;
    private String connectionSource;
    private String ddlType;
    private String errorMessage;

    public ExecuteResponse( JSONArray data,String connectionSource, String ddlType ){
        this.data = data.toList();
        this.connectionSource = connectionSource;
        this.ddlType = ddlType;
    }

}
