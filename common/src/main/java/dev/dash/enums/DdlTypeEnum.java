package dev.dash.enums;

public enum DdlTypeEnum {
    Select,
    Insert,
    Update,
    Delete;

    public static DdlTypeEnum findType(String s){
        if(s.equalsIgnoreCase(Select.name())){
            return Select;
        } else if(s.equalsIgnoreCase(Insert.name())){
            return Insert;
        } else if(s.equalsIgnoreCase(Update.name())){
            return Update;
        } else if(s.equalsIgnoreCase(Delete.name())){
            return Delete;
        }
        return Select;
    }

}
