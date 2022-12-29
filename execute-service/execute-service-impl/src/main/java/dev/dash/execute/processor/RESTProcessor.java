package dev.dash.execute.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import dev.dash.execute.util.QueryStringParser;
import dev.dash.model.ConnectionConfig;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.security.AuditLogicService;
import dev.dash.util.StringUtil;
import lombok.extern.slf4j.Slf4j;


import org.json.JSONArray;
import org.json.JSONObject;

@Slf4j
@Service
public class RESTProcessor implements ResourceProcessor {

    @Autowired
    AuditLogicService auditLogicService;

    @Override
    public JSONArray processQuery(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> request;
        if(StringUtil.isVaildString(queryConfig.getQueryString())){
            String query = QueryStringParser.parseAndReplaceQueryString( queryConfig, executionData );
            request = new HttpEntity<String>(query, headers);
        } else {
            request = new HttpEntity<String>(headers);
        }
        
        ResponseEntity<String> response = restTemplate.exchange(
            assembleUrl(queryConfig,connectionConfig,executionData), 
            resolveMethod(queryConfig), 
            request, String.class);
        
        JSONArray arr = new JSONArray();
        if(response.hasBody()) {
            JSONObject body = new JSONObject(response.getBody());
            return arr.put(body);
        }
        return arr;
    }

    HttpMethod resolveMethod(QueryConfig queryConfig){
        return HttpMethod.resolve(queryConfig.getDdlType());
    }

    String assembleUrl(QueryConfig queryConfig, ConnectionConfig connectionConfig, ExecutionData executionData) {
        StringBuilder sb = new StringBuilder(connectionConfig.getUrl());
        if(StringUtil.isVaildString(queryConfig.getPath())){
            sb.append(queryConfig.getPath());
        }
        return sb.toString();
    }

}