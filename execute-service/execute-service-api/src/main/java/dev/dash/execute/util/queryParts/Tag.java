package dev.dash.execute.util.queryParts;

import lombok.Data;

@Data
public class Tag {
    private int startIndex;
    private int endIndex;
    private String tagName; // todo enum
    private boolean endTag = false;

    public Tag(int startIndex, int endIndex, String tagName) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.tagName = tagName;
        if(tagName.indexOf("/") == 0){
            this.endTag = true;
        }
    }

    public Tag(int startIndex, int endIndex, String tagName, boolean endTag) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.tagName = tagName;
        this.endTag = endTag;
    }
}
