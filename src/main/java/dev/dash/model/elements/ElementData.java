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
    private String value;
}
