package dev.dash.util;

import org.json.JSONObject;

public class JsonUtil {
    
    public static String toJSON(Object obj){

        JSONObject jsonObj = new JSONObject(obj);

        return jsonObj.toString();
    }

}
