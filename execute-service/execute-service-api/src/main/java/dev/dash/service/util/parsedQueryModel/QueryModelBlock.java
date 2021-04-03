package dev.dash.service.util.parsedQueryModel;

import java.util.Map;

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
     */
    default String replaceReplacementCode(String str, String replacementCode, String value) {
        return str.replaceAll("\\$\\{"+replacementCode+"\\}", value);
    }
}