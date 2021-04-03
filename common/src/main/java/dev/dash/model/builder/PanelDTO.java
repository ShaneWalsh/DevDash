package dev.dash.model.builder;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import lombok.AllArgsConstructor;
import lombok.Data;

@JsonDeserialize(using = PanelDTODeserializer.class)
@AllArgsConstructor
@Data
public class PanelDTO {
    private String code; 
    private String name;
    private Integer positionX;
    private Integer positionY;
    private Integer columnSizeX;
    private Integer columnSizeY;    
    private boolean showRefresh;
    private String elements;
    private String tabConfig;
}

class PanelDTODeserializer extends StdDeserializer<PanelDTO> { 

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public PanelDTODeserializer() {
        this(null); 
    } 

    public PanelDTODeserializer(Class<?> vc) { 
        super(vc); 
    }

    @Override
    public PanelDTO deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        String code = node.get("code").asText();
        String name = node.get("name").asText();
        Integer positionX = 0;//node.get("positionX").asInt();
        Integer positionY = 0;//node.get("positionY").asInt();
        Integer columnSizeX = 0;// node.get("columnSizeX").asInt();
        Integer columnSizeY = 0;//node.get("columnSizeY").asInt();
        Boolean showRefresh = false; //node.get("showRefresh").asBoolean();
        JsonNode arr = node.get("elements");
        String elements = arr.toString();
        String tabConfig = node.get("tabConfig").asText();

        return new PanelDTO(code,name,positionX,positionY,columnSizeX,columnSizeY,showRefresh,elements,tabConfig);
    }
}
