package dev.dash.execute.util.queryParts;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class LogicBlock {

    private Tag startTag;
    private Tag endTag;
    private List<LogicBlock> nestedBlocks = new ArrayList<>();

    public LogicBlock(Tag startTag) {
        this.startTag = startTag;
    }

    public boolean isEndTag(Tag tag) {
        return tag.isEndTag();
    }

    public void addNestedBlock(LogicBlock block) {
        this.nestedBlocks.add(block);
    }
}