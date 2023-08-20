package dev.dash.service.util.parsedQueryModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.dash.execute.util.QueryStringParser;
import dev.dash.execute.util.parsedQueryModel.ParsedQueryModel;
import dev.dash.model.elements.ElementData;

public class ComplexQueryModelBlockTest {

    @Test
    void repeatableQueryModelBlock_simple_Test() {
        ParsedQueryModel extractParsedQueryModel = QueryStringParser.extractParsedQueryModel("{ \"body\": { <optionalclause><clause>\"statusCd\": \"${PL_WF_statusCd_Code}\",</clause></optionalclause> <repeatable><joiner>,</joiner><clause>\"${PL_WF_entityModelNames_Code}\"</clause><pfix>\"entityModelNames\":[</pfix><sfix>]</sfix></repeatable>  } \"requestId\": \"string\", \"finalResponse\": true }");
        
        Map<String, ElementData> mappy = new HashMap<>();
        String stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("{ \"body\": {    } \"requestId\": \"string\", \"finalResponse\": true }", stringQuery);

        // empty value is still replaced
        mappy.put("PL_WF_statusCd_Code", new ElementData("PL_WF_statusCd_Code", "PL_WF_statusCd_Code", "String", ""));
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("{ \"body\": { \"statusCd\": \"\",   } \"requestId\": \"string\", \"finalResponse\": true }", stringQuery);
        
        mappy = new HashMap<>();
        mappy.put("PL_WF_statusCd_Code", new ElementData("PL_WF_statusCd_Code", "PL_WF_statusCd_Code", "String", "1990"));
        
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("{ \"body\": { \"statusCd\": \"1990\",   } \"requestId\": \"string\", \"finalResponse\": true }", stringQuery);
        
        mappy.put("PL_WF_entityModelNames_Code", new ElementData("PL_WF_entityModelNames_Code", "PL_WF_entityModelNames_Code", "String", "One,Two"));
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("{ \"body\": { \"statusCd\": \"1990\", \"entityModelNames\":[\"One\",\"Two\"]  } \"requestId\": \"string\", \"finalResponse\": true }", stringQuery);
        
    }
    
}
