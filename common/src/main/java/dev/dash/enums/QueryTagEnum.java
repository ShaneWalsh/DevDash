package dev.dash.enums;

public enum QueryTagEnum {
    StringQuery(""),
    OptionalQuery("optionalclause"),
    RepeatableQuery("repeatable");

    private String openingTag;

	private QueryTagEnum(String openingTag) {
		this.openingTag = openingTag;
    }

    public static QueryTagEnum find(String tagName){
        if(QueryTagEnum.OptionalQuery.getOpeningTag().equalsIgnoreCase(tagName)){
            return QueryTagEnum.OptionalQuery;
        } else if(QueryTagEnum.RepeatableQuery.getOpeningTag().equalsIgnoreCase(tagName)){
            return QueryTagEnum.RepeatableQuery;
        } else {
            return QueryTagEnum.StringQuery;
        }
    }

    public String getOpeningTag() {
        return openingTag;
    }
}
