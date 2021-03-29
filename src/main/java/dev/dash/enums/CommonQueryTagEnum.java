package dev.dash.enums;

public enum CommonQueryTagEnum {
    clause("clause"),
    suffix("sfix"),
    prefix("pfix");

    private String tagCode;

    private CommonQueryTagEnum(String tagCode) {
        this.tagCode = tagCode;
    }

    public static CommonQueryTagEnum find(String tagName){
        if(CommonQueryTagEnum.clause.getTagCode().equalsIgnoreCase(tagName)){
            return CommonQueryTagEnum.clause;
        } else if(CommonQueryTagEnum.suffix.getTagCode().equalsIgnoreCase(tagName)){
            return CommonQueryTagEnum.suffix;
        } else if(CommonQueryTagEnum.prefix.getTagCode().equalsIgnoreCase(tagName)){
            return CommonQueryTagEnum.prefix;
        }
        return null;
    }

    public String getTagCode() {
        return tagCode;
    }
}
