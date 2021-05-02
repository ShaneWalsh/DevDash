package dev.dash.execute.util.parsedQueryModel;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dev.dash.enums.CommonQueryTagEnum;
import dev.dash.execute.util.queryParts.LogicBlock;
import lombok.Data;

@Data
public abstract class CommonQueryBlock implements QueryModelBlock {

    private String clause;
    private Set<String> replacementCodes = new HashSet<>(1);
    private String suffix = "";
    private String prefix = "";

    /**
     * Try to parse some of the common tags
     */
    protected boolean parseCommonTag(String queryString, LogicBlock logicBlock) {
        CommonQueryTagEnum common = CommonQueryTagEnum.find(logicBlock.getStartTag().getTagName());
        if(common != null) {
            switch (common) {
                case suffix : setSuffix(getTagContents(queryString, logicBlock));break;
                case prefix : setPrefix(getTagContents(queryString, logicBlock));break;
                case clause : setClause(getTagContents(queryString, logicBlock));break;
            }
            return true;
        }
        return false;
    }

    /**
     * Set the clause and try to extract the replacement codes from the clause
     * @param clause
     */
    public void setClause(String clause) {
        this.clause = clause;
        Pattern pattern = Pattern.compile("\\$\\{([\\S]*)\\}");
        Matcher matcher = pattern.matcher(clause); //this worked on regex101 \${([\S]*)}

        while(matcher.find()) {
            System.out.println("found: " + matcher.group(1));
            replacementCodes.add(matcher.group(1));
        }
    }

}
