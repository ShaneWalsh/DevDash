package dev.dash.service.util.parsedQueryModel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.dash.model.elements.ElementData;
import dev.dash.service.util.QueryStringParser;

public class RepeatableQueryModelBlockTest {

    @Test
    void repeatableQueryModelBlock_simple_Test() {
        ParsedQueryModel extractParsedQueryModel = QueryStringParser.extractParsedQueryModel("SELECT * FROM tutorials_tbl where tutorial_id = ${tutIdFromDate}<repeatable><joiner> OR </joiner><clause>author_name LIKE '${author_name}'</clause><pfix> AND ( </pfix><sfix> ) </sfix></repeatable>");
        
        List<QueryModelBlock> queryModelBlockList = extractParsedQueryModel.getQueryModelBlockList();
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = ${tutIdFromDate}", ((StringQueryModelBlock) queryModelBlockList.get(0)).getQueryString());

        Map<String, ElementData> mappy = new HashMap<>();
        mappy.put("tutIdFromDate", new ElementData("tutIdFromDateCode", "tutIdFromDate", "String", "1990"));

        String stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1990", stringQuery);

        // no replacement should be performed.
        stringQuery = extractParsedQueryModel.processElementData(new HashMap<>());
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = ${tutIdFromDate}", stringQuery);

        
        mappy = new HashMap<>();
        mappy.put("tutIdFromDate", new ElementData("tutIdFromDateCode", "tutIdFromDate", "String", "1992"));
        mappy.put("author_name", new ElementData("author_nameCode", "author_name", "String", "Frenchy"));

        // single replacement
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1992 AND ( author_name LIKE 'Frenchy' ) ", stringQuery);

        // multiple replacement
        mappy.put("author_name", new ElementData("author_nameCode", "author_name", "String", "Frenchy,Tom,Owen"));
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1992 AND ( author_name LIKE 'Frenchy' OR author_name LIKE 'Tom' OR author_name LIKE 'Owen' ) ", stringQuery);

        // multiple + single replacement
        extractParsedQueryModel = QueryStringParser.extractParsedQueryModel("SELECT * FROM tutorials_tbl where tutorial_id = ${tutIdFromDate}<repeatable><joiner> OR </joiner><clause>(author_name LIKE '${author_name}' OR ${tutIdFromDate} )</clause><pfix> AND ( </pfix><sfix> ) </sfix></repeatable>");
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1992 AND ( (author_name LIKE 'Frenchy' OR 1992 ) OR (author_name LIKE 'Tom' OR 1992 ) OR (author_name LIKE 'Owen' OR 1992 ) ) ", stringQuery);

        // Test that the fail logic condition if one of the required RC is missing
        mappy = new HashMap<>();
        mappy.put("tutIdFromDate", new ElementData("tutIdFromDateCode", "tutIdFromDate", "String", "1992"));
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = 1992", stringQuery);

        mappy = new HashMap<>();
        mappy.put("author_name", new ElementData("author_nameCode", "author_name", "String", "Frenchy"));
        stringQuery = extractParsedQueryModel.processElementData(mappy);
        assertEquals("SELECT * FROM tutorials_tbl where tutorial_id = ${tutIdFromDate}", stringQuery);
    }
    
}
