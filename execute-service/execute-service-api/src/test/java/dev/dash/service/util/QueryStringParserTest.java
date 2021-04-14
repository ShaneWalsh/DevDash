package dev.dash.service.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.dash.model.elements.ElementData;
import dev.dash.service.util.parsedQueryModel.OptionalQueryModelBlock;
import dev.dash.service.util.parsedQueryModel.ParsedQueryModel;
import dev.dash.service.util.parsedQueryModel.QueryModelBlock;
import dev.dash.service.util.parsedQueryModel.StringQueryModelBlock;

public class QueryStringParserTest {


    @Test
    void extractParsedQueryModel_simple_Test() {
        ParsedQueryModel extractParsedQueryModel = QueryStringParser.extractParsedQueryModel("SELECT * FROM tutorials_tbl where tutorial_id = ${tutIdFromDate}");
        
        List<QueryModelBlock> queryModelBlockList = extractParsedQueryModel.getQueryModelBlockList();
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = ${tutIdFromDate}", ((StringQueryModelBlock) queryModelBlockList.get(0)).getQueryString());

        Map<String, ElementData> mappy = new HashMap<>();
        mappy.put("tutIdFromDate", new ElementData("tutIdFromDateCode", "tutIdFromDate", "String", "1990"));

        String stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1990", stringQuery);

        // no replacement should be performed.
        stringQuery = extractParsedQueryModel.processElementData(new HashMap<>());
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = ${tutIdFromDate}", stringQuery);
    }

    @Test
    void extractParsedQueryModel_sanity_Test() {
        ParsedQueryModel extractParsedQueryModel = QueryStringParser.extractParsedQueryModel("SELECT * FROM tutorials_tbl <optionalclause><pfix>where </pfix><clause>tutorial_id = ${tutIdFromDate}</clause><sfix> some</sfix></optionalclause> more code ${randomValue}");
        List<QueryModelBlock> queryModelBlockList = extractParsedQueryModel.getQueryModelBlockList();

        assertEquals("SELECT * FROM tutorials_tbl ", ((StringQueryModelBlock) queryModelBlockList.get(0)).getQueryString());
        assertEquals("where ", ((OptionalQueryModelBlock) queryModelBlockList.get(1)).getPrefix());
        assertEquals("tutorial_id = ${tutIdFromDate}", ((OptionalQueryModelBlock) queryModelBlockList.get(1)).getClause());
        assertEquals(" some", ((OptionalQueryModelBlock) queryModelBlockList.get(1)).getSuffix());
        assertEquals(true, ((OptionalQueryModelBlock) queryModelBlockList.get(1)).getReplacementCodes().contains("tutIdFromDate"));
        assertEquals(" more code ${randomValue}", ((StringQueryModelBlock) queryModelBlockList.get(2)).getQueryString());

        Map<String, ElementData> mappy = new HashMap<>();
        mappy.put("tutIdFromDate", new ElementData("tutIdFromDateCode", "tutIdFromDate", "String", "1990"));
        mappy.put("randomValue", new ElementData("randomValueCode", "randomValue", "String", "randomValueStr"));

        String stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1990 some more code randomValueStr", stringQuery);

        // the optional clause should be not be present
        stringQuery = extractParsedQueryModel.processElementData(new HashMap<>());
        assertEquals("SELECT * FROM tutorials_tbl  more code ${randomValue}", stringQuery);
    }

    @Test
    void extractParsedQueryModel_multiple_same_rc_Test() {
        ParsedQueryModel extractParsedQueryModel = QueryStringParser.extractParsedQueryModel("SELECT * FROM tutorials_tbl <optionalclause><clause>where tutorial_id = ${tutIdFromDate} and p=${tutIdFromDate}</clause></optionalclause> some more code");

        Map<String, ElementData> mappy = new HashMap<>();
        mappy.put("tutIdFromDate", new ElementData("tutIdFromDateCode", "tutIdFromDate", "String", "1990"));

        String stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1990 and p=1990 some more code", stringQuery);

        mappy.put("tutIdFromDate", new ElementData("tutIdFromDateCode", "tutIdFromDate", "String", "1991"));
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1991 and p=1991 some more code", stringQuery);
    }

    /**
     * Ive had some issues with ' and \ in the query strings for the Insert/Update/Delete queries because I dont have Prepared Statements implemented yet.
     * To insert " and ' and \ in string queries, "=\" and '='' and \=\\ 
     */
    @Test
    void extractParsedQueryModel_escapse_characters_in_querystr_Test() {
        ParsedQueryModel extractParsedQueryModel = QueryStringParser.extractParsedQueryModel("insert into test (q1,q2) values('${stringWithSingleQuotes}','${stringWithBackSlash}')");

        Map<String, ElementData> mappy = new HashMap<>();
        mappy.put("stringWithSingleQuotes", new ElementData("stringWithSingleQuotes", "stringWithSingleQuotes", "String", "update test set code='${DD_Configurator_Dashboard_Update_F_Code}' where dashboardconfig_id = ${DD_Configurator_Dashboard_Update_F_Id}"));
        mappy.put("stringWithBackSlash", new ElementData("stringWithBackSlash", "stringWithBackSlash", "String", 
        "["+
            "{\"code\":\"DD_Configurator_Panel_Update_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"code\\\"}\"},"+
            "{\"name\":\"DD_Configurator_Panel_Update_F_name\",\"type\":\"TEXT\",\"label\":\"name\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\"tableRowColumnId\\\":\\\"name\\\"}\"}"+
        "]"));

        

        String stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("insert into test (q1,q2) values("+
            "'update test set code=''${DD_Configurator_Dashboard_Update_F_Code}'' where dashboardconfig_id = ${DD_Configurator_Dashboard_Update_F_Id}',"+
            "'[{\"code\":\"DD_Configurator_Panel_Update_F_Code\",\"type\":\"TEXT\",\"label\":\"Code\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\\\"tableRowColumnId\\\\\":\\\\\"code\\\\\"}\"},"+
              "{\"name\":\"DD_Configurator_Panel_Update_F_name\",\"type\":\"TEXT\",\"label\":\"name\", \"dataOn\":\"DD_Configurator_Panel_List_Table1\",\"dataOnParser\":\"StringParser\",\"dataOnParserConfig\":\"{\\\\\"tableRowColumnId\\\\\":\\\\\"name\\\\\"}\"}]')",
            stringQuery);

        
    }
}
