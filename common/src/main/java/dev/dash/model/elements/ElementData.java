package dev.dash.model.elements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElementData {
    private String code; 
    private String replacementCode; 
    private String elementDataType; 
    private Object value;

    public String getStringValue() {
        if(value != null){
            return value.toString();
        }
        return null;
    }
}
