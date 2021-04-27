package dev.dash.service.util.parsedQueryModel;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.dash.model.elements.ElementData;
import dev.dash.service.util.queryParts.LogicBlock;

public interface QueryModelBlock {
    
    public String processElementData(Map<String,ElementData> replacementCodeMap);
    public String toStringQuery();

    /**
     * Simple utility method I am keeping one place in case it needs to change or be overridden.
     */
    default String getTagContents(String queryString, LogicBlock logicBlock ) {
        return queryString.substring(logicBlock.getStartTag().getEndIndex()+1,logicBlock.getEndTag().getStartIndex());
    }

    /**
     * Classic replacement of the RC
     * Does a simple replacement for sql queries for single quotes.
     */
    default String replaceReplacementCode(String str, String replacementCode, String value) {
        // TODO add null check here on value, it should not be null, what do we do in this scenario?
        String valueSafeSQL = value.replaceAll("'", "''");
        valueSafeSQL = valueSafeSQL.replaceAll(Pattern.quote("\\"), Matcher.quoteReplacement("\\\\")); // TODO remove both of these escapses once we move the Insert/Update/Delete to prepared statements
        return str.replaceAll(Pattern.quote("${"+replacementCode+"}"), Matcher.quoteReplacement(valueSafeSQL));
    }
}