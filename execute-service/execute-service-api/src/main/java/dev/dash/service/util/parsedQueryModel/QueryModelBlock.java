package dev.dash.service.util.parsedQueryModel;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

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
        String valueSafeSQL = value.replaceAll("'", "''");
        return str.replaceAll(Pattern.quote("${"+replacementCode+"}"), Matcher.quoteReplacement(valueSafeSQL));
    }
}