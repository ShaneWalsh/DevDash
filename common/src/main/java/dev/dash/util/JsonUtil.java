package dev.dash.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {
    
    private static final String JSON_STRING_VALUE = "{\"%s\":\"%s\"}";

    public static String toJSON(Object obj){

        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Unable to marshall Pojo for audit logging", e);
        }
        // backup marshalling
        JSONObject jsonObj = new JSONObject(obj);
        return jsonObj.toString();
    }

    /**
     * Sometimes we just want to store a single variable in the JSON data
     */
    public static String stringProp(String key, String value) {
        return String.format(JSON_STRING_VALUE, key, value);
    }

}
