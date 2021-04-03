package dev.dash.service.util.parsedQueryModel;

import java.util.Map;

import dev.dash.model.elements.ElementData;
import dev.dash.service.util.queryParts.LogicBlock;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/*
e.g
<optionalclause>
    <clause>author_name LIKE ‘${replacementCode}’</clause> 
    <rcodes> // Actually I think these can be infered from the contents of the clause.
        <rc>replacementCode</rc>
    </rcodes>
    <sfix>AND</sfix>
    <pfix>AND</pfix>
</optionalclause>
*/

@Slf4j
@Data
public class OptionalQueryModelBlock extends CommonQueryBlock {

    public OptionalQueryModelBlock(String queryString, LogicBlock block) {
        for(LogicBlock nestedBlock :block.getNestedBlocks()){
            boolean foundCommonTag = parseCommonTag(queryString, nestedBlock);
            if( !foundCommonTag ) {
                // look for a custom one? 
            }
        }
	}

    @Override
    public String toStringQuery() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Will only include its changes if the required replacementCodes are present
     */
    @Override
    public String processElementData(Map<String,ElementData> replacementCodeMap) {
        String clauseTemp = getClause();
        for(String rc : getReplacementCodes()){
            if(replacementCodeMap.containsKey(rc)){
                clauseTemp = replaceReplacementCode(clauseTemp, rc, replacementCodeMap.get(rc).getValue());
            } else {
                log.warn("Failed to supply all of the required rc: {} for optional clause: {}", rc, getClause());
                return "";
            }
        }
        return getPrefix() + clauseTemp + getSuffix();
    }
    
}