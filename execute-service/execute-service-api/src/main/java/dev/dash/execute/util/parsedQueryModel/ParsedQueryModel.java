package dev.dash.execute.util.parsedQueryModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.dash.model.elements.ElementData;

public class ParsedQueryModel {
    private List<QueryModelBlock> queryModelBlockList;

    public ParsedQueryModel() {
        this.queryModelBlockList = new ArrayList<QueryModelBlock>();
    }

    public List<QueryModelBlock> getQueryModelBlockList(){
        return queryModelBlockList;
    }

    public ParsedQueryModel addQueryModelBlock(QueryModelBlock queryModelBlock){
        this.queryModelBlockList.add(queryModelBlock);
        return this;
    } 

    public String processElementData(Map<String,ElementData> replacementCodeMap) {
        StringBuilder sb = new StringBuilder();
        queryModelBlockList.stream().forEach(queryModelBlock -> sb.append(queryModelBlock.processElementData(replacementCodeMap)));
		return sb.toString();
	}

    @Deprecated
    public String toStringQuery(){
        StringBuilder sb = new StringBuilder();
        queryModelBlockList.stream().forEach(query -> sb.append(query.toStringQuery()));
        return sb.toString();
    }
}