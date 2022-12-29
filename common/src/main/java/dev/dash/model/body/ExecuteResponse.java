package dev.dash.model.body;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Simple Response wrapper to include more information about the datasource for the UI to parse it properly.
 */
@Data
@AllArgsConstructor
public class ExecuteResponse {
    private Object data;
    private String connectionSource;
    private String ddlType;
    private String errorMessage;

    public ExecuteResponse( Object data,String connectionSource, String ddlType ){
        this.data = data;
        this.connectionSource = connectionSource;
        this.ddlType = ddlType;
    }
}
