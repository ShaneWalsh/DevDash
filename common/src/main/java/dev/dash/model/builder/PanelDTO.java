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
    private Long id;
    private String code; 
    private String name;
    private Integer gridRow;
    private Integer gridCol;
    private String elements;
    private String tabConfig;
    private String securityRole;
}

class PanelDTODeserializer extends StdDeserializer<PanelDTO> { 

    private static final long serialVersionUID = 11223432L;

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
        
        Long id = node.has("id") ? node.get("id").asLong(): null;
        String code = node.get("code").asText();
        String name = node.get("name").asText();
        Integer gridRow = node.has("gridRow") ? node.get("gridRow").asInt() : 0;
        Integer gridCol = node.has("gridCol") ? node.get("gridCol").asInt() : 0;
        JsonNode arr = node.has("elements") ? node.get("elements") : null;
        String elements = arr != null ? arr.toString() : "";
        String tabConfig = node.has("tabConfig") ? node.get("tabConfig").asText(): null;
        String securityRole = node.has("securityRole") ? node.get("securityRole").asText(): null;

        return new PanelDTO(id, code, name, gridRow, gridCol, elements, tabConfig, securityRole);
    }
}
