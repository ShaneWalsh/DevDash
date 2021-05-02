package dev.dash.execute.util.parsedQueryModel;

import java.util.Map;

import dev.dash.execute.util.queryParts.LogicBlock;
import dev.dash.model.elements.ElementData;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/*
<repeatable>
    <joiner>OR</joiner>
    <clause>author_name LIKE ‘${replacementCode}’</clause>
    <rc>replacementCode</rc> // will be inferred.
    <pfix>AND (</pfix>
    <sfix>) AND</sfix>
</repeatable>
*/

@Slf4j
@Data
public class RepeatableQueryModelBlock extends CommonQueryBlock {

    private String joiner;

    public RepeatableQueryModelBlock(String queryString, LogicBlock logicBlock) {
        for(LogicBlock nestedBlock :logicBlock.getNestedBlocks()){
            boolean foundCommonTag = parseCommonTag(queryString, nestedBlock);
            if( !foundCommonTag ) {
                RepeatableQueryTagEnum repeatable = RepeatableQueryTagEnum.find(nestedBlock.getStartTag().getTagName());
                if(repeatable != null) {
                    switch (repeatable) {
                        case Joiner : setJoiner(getTagContents(queryString, nestedBlock));break;
                    }
                    continue;
                } else {
                    log.warn("Unable to parse this logic block:{}",nestedBlock.getStartTag().getTagName());
                }
            }
        }
	}

    @Override
    public String toStringQuery() {
        return null;
    }

    /**
     * Will only include its changes if the required replacementCodes are present
     * Will check to see if the RC is a a comma delimited list, if it is, the value will be added multiple times split by the joiner.
     * Its possible to mix multiple single RC with one multiple RC.
     * Two Multiple RC would not currently be supported.
     */
    @Override
    public String processElementData(Map<String,ElementData> replacementCodeMap) {
        String totalClause = "";
        String clauseTemp = getClause();
        for(String rc : getReplacementCodes()){
            if(replacementCodeMap.containsKey(rc)){
                String rcValue = replacementCodeMap.get(rc).getValue();
                String[] rcValueSplit = rcValue.split(",");
                if(rcValueSplit.length > 1) {
                    boolean first = true;
                    for(String value : rcValueSplit) {
                        totalClause = (!first) ? (totalClause+getJoiner()) : totalClause; // add in the Joiner each loop.
                        totalClause += replaceReplacementCode(clauseTemp, rc, value);
                        first = false;
                    }
                } else {
                    if(totalClause.equalsIgnoreCase("")){ // total clause is not set yet
                        clauseTemp = replaceReplacementCode(clauseTemp, rc, replacementCodeMap.get(rc).getValue());
                    } else { // the clause is already populated, and maybe even has joiners, so replace the RC across the whole clause+joiners
                        totalClause = replaceReplacementCode(totalClause, rc, rcValue);
                    }
                }
            } else {
                log.warn("Failed to supply all of the required rc: {} for repeatable clause: {}", rc, getClause());
                return "";
            }
        }
        //if the joiner was never used then we may just have a single clause to return; Muliple Joiner loops not supported.
        return (!totalClause.equalsIgnoreCase(""))? getPrefix() + totalClause + getSuffix() : getPrefix() + clauseTemp + getSuffix();
    }
    
}

enum RepeatableQueryTagEnum {
    Joiner("joiner");

    private String tagCode;

    private RepeatableQueryTagEnum(String tagCode) {
        this.tagCode = tagCode;
    }

    public static RepeatableQueryTagEnum find(String tagName){
        if(RepeatableQueryTagEnum.Joiner.getTagCode().equalsIgnoreCase(tagName)){
            return RepeatableQueryTagEnum.Joiner;
        }
        return null;
    }

    public String getTagCode() {
        return tagCode;
    }
}