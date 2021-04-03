package dev.dash.service.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.dash.enums.QueryTagEnum;
import dev.dash.model.QueryConfig;
import dev.dash.model.body.ExecutionData;
import dev.dash.model.elements.ElementData;
import dev.dash.service.util.parsedQueryModel.OptionalQueryModelBlock;
import dev.dash.service.util.parsedQueryModel.ParsedQueryModel;
import dev.dash.service.util.parsedQueryModel.RepeatableQueryModelBlock;
import dev.dash.service.util.parsedQueryModel.StringQueryModelBlock;
import dev.dash.service.util.queryParts.LogicBlock;
import dev.dash.service.util.queryParts.Tag;

public class QueryStringParser {
    
    public QueryStringParser() {
    }

    /**
     * basic string replacement, will check if any of the specfied fields need to be replaced in this query
     * not very efficent and should be refactored to find the required ${} varibles and then get the value from a map.
     */
    public static String parseAndReplaceQueryString(QueryConfig queryConfig, ExecutionData executionData ){
        String queryString = queryConfig.getQueryString();
        List<ElementData> allData = new ArrayList<>(); // todo, is this even required anymore?
        Map<String,ElementData> replacementCodeMap = new HashMap<>();
        if(executionData != null && executionData.getPanelElementData() != null) {
            Map<String, List<ElementData>> panelElementData = executionData.getPanelElementData();
            for(String key:panelElementData.keySet()){
                List<ElementData> elementDatas =  panelElementData.get(key);
                elementDatas.stream().forEach(data -> replacementCodeMap.put(data.getReplacementCode(), data));
                allData.addAll(elementDatas);
            }

            ParsedQueryModel parsedQueryModel = extractParsedQueryModel(queryString);
            queryString = parsedQueryModel.processElementData(replacementCodeMap);
        }
        return queryString;
    }

    public static ParsedQueryModel extractParsedQueryModel(String queryString){
        ParsedQueryModel parsedQueryModel = new ParsedQueryModel();
        // complexity here, as I want to find the optionals in the query and sort those out first
        // parse the query for logic blocks <optionalclause> and <repeatable>
        List<LogicBlock> blocks = new ArrayList<>();
        if(queryString.indexOf("<", 0) > -1){
            int currentIndex = 0;
            while(currentIndex < queryString.length()){
                // get a tag, 
                Tag tag = getNextTag(queryString,currentIndex);
                if(tag != null){
                    LogicBlock blocky = new LogicBlock(tag);
                    Tag endTag = getEndTag(queryString,tag.getEndIndex(),blocky);
                    if(endTag != null){
                        blocky.setEndTag(endTag);
                        blocks.add(blocky);
                        currentIndex = endTag.getEndIndex()+1;
                    } else { // else it must have been a false positve, not actually a tag
                        currentIndex = tag.getEndIndex()+1;
                    }
                } else { // no more tags to extract
                    break;
                }
            }
        }
        
        // now work out all of the static string content between the blocks and put those into string blocks
        int currentIndex = 0;
        if(blocks.size() > 0){
            for(LogicBlock block : blocks){
                currentIndex = 0;
                Tag startTag = block.getStartTag();
                int startingTagIndex = startTag.getStartIndex();
                if(startingTagIndex > currentIndex){
                    parsedQueryModel.addQueryModelBlock(new StringQueryModelBlock(queryString.substring(currentIndex, startingTagIndex)));
                }
                QueryTagEnum queryTagEnum = QueryTagEnum.find(startTag.getTagName());
                switch ( queryTagEnum ) {
                    case OptionalQuery: parsedQueryModel.addQueryModelBlock(new OptionalQueryModelBlock(queryString, block)); break;
                    case RepeatableQuery: parsedQueryModel.addQueryModelBlock(new RepeatableQueryModelBlock(queryString, block)); break;
                    case StringQuery: parsedQueryModel.addQueryModelBlock(new StringQueryModelBlock(queryString)); break;
                }
                currentIndex = block.getEndTag().getEndIndex()+1;
            }
            if(currentIndex < queryString.length()){
                parsedQueryModel.addQueryModelBlock(new StringQueryModelBlock(queryString.substring(currentIndex)));
            }
        } else { // no logic blocks, so we have just a normal query
            parsedQueryModel.addQueryModelBlock(new StringQueryModelBlock(queryString));
        }

        return parsedQueryModel;
    }

    /**
     * Recursive function to find end tag for the current logic block.
     * If another tag is found a new level of recursion will begin looking for the end tag for that tag, etc etc.
     * Finding the end tags break the recursion and pass back up the stack.
     */
    public static Tag getEndTag(String queryStr, int beginningIndex, LogicBlock currentBlock) {
        int currentIndex = beginningIndex;
        while(currentIndex < queryStr.length()){
            Tag tag = getNextTag(queryStr,currentIndex);
            if(tag != null) {
                if(isValidTag(tag)) {
                    if(currentBlock.isEndTag(tag)){
                        return tag;
                    } else {
                        LogicBlock nestedBlock = new LogicBlock(tag);
                        Tag nestedTagEnd = getEndTag(queryStr,tag.getEndIndex(),nestedBlock);
                        if(nestedTagEnd != null){
                            nestedBlock.setEndTag(nestedTagEnd);
                            currentBlock.addNestedBlock(nestedBlock);
                            currentIndex = nestedTagEnd.getEndIndex()+1;
                        } else { // else it must have been a false positve, not actually a tag
                            currentIndex = tag.getEndIndex()+1;
                        }
                    }
                } else { // invalid tag, skip past it.
                    currentIndex = tag.getEndIndex()+1;
                }
            } else { // no tags, end the search
                break;
            }
        }
        return null;
    }

    private static boolean isValidTag(Tag tag) {
        return true; // todo check for validity of the tags errr
    }

    /**
     * simple function to extract the next tag value
     */
    public static Tag getNextTag(String queryStr, int beginningIndex){
        int indexOfStartingTagBegin = queryStr.indexOf("<", beginningIndex);
        int indexOfStartingTagEnd = queryStr.indexOf(">", beginningIndex+1);
        if(indexOfStartingTagBegin > -1 && indexOfStartingTagEnd > -1){
            String tagName = queryStr.substring(indexOfStartingTagBegin+1, indexOfStartingTagEnd);
            return new Tag(indexOfStartingTagBegin,indexOfStartingTagEnd,tagName);
        } else { // no more tags
            return null;
        }
    }

}