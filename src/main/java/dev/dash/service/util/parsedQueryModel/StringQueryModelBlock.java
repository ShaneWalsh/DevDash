package dev.dash.service.util.parsedQueryModel;

import java.util.Map;

import dev.dash.model.elements.ElementData;

public class StringQueryModelBlock implements QueryModelBlock {

    final String queryString;

    public StringQueryModelBlock(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString(){
        return this.queryString;
    }

    @Override
    public String toStringQuery() {
        return queryString;
    }

    @Override
    public String processElementData(Map<String,ElementData> replacementCodeMap) {
        String queryStringTemp = queryString;
        // todo refactor this and make it more efficent
        for(ElementData elementData:replacementCodeMap.values()) {
            queryStringTemp = replaceReplacementCode(queryStringTemp,elementData.getReplacementCode(),elementData.getValue());
        }
        return queryStringTemp;
    }
   
}