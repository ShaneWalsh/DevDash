package dev.dash.model.builder;

import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonDeserialize(using = PanelDTODeserializer.class)
@JsonSerialize(using = PanelDTOSerializer.class)
@AllArgsConstructor
@NoArgsConstructor
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

class PanelDTOSerializer extends StdSerializer<PanelDTO> {
    
    private static final long serialVersionUID = 12223432L;

    public PanelDTOSerializer() {
        this(null);
    }
  
    public PanelDTOSerializer(Class<PanelDTO> t) {
        super(t);
    }

    @Override
    public void serialize(
        PanelDTO value, JsonGenerator jgen, SerializerProvider provider) 
      throws IOException, JsonProcessingException {
        
        jgen.writeStartObject();
        jgen.writeStringField("code", value.getCode());
        jgen.writeStringField("name", value.getName());
        jgen.writeNumberField("gridRow", value.getGridRow());
        jgen.writeNumberField("gridCol", value.getGridCol());
        ObjectMapper om = new ObjectMapper();
        ArrayNode node = (ArrayNode) om.readTree(value.getElements());
        
        jgen.writeFieldName("elements");
        jgen.writeStartArray();
        Iterator<JsonNode> it = node.elements();
        while(it.hasNext()){
            JsonNode panelElement = it.next();
            jgen.writeTree(panelElement);
        }
        jgen.writeEndArray();
        jgen.writeStringField("tabConfig", value.getTabConfig());
        jgen.writeStringField("securityRole", value.getSecurityRole());
        jgen.writeEndObject();
    }
}