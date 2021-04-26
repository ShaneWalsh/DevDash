package dev.dash.model.body;

import java.util.List;
import java.util.Map;

import dev.dash.model.elements.ElementData;
import lombok.Data;

@Data
public class ExecutionData {
    private String dashboardCode;
    private String tabConfigCode;
    private String elementSourcePanelCode;
    private Map<String,List<ElementData>> panelElementData;
}
