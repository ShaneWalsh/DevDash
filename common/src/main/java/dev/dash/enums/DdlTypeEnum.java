package dev.dash.enums;

/**
 * Mix of ddl methods used by different connection sources.
 */
public enum DdlTypeEnum {
    Select,
    Insert,
    Update,
    Delete,
    GET,
    POST,
    PUT;

    public static DdlTypeEnum findType(String s){
        if(s.equalsIgnoreCase(Select.name())){
            return Select;
        } else if(s.equalsIgnoreCase(Insert.name())){
            return Insert;
        } else if(s.equalsIgnoreCase(Update.name())){
            return Update;
        } else if(s.equalsIgnoreCase(Delete.name())){
            return Delete;
        }else if(s.equalsIgnoreCase(GET.name())){
            return GET;
        }else if(s.equalsIgnoreCase(POST.name())){
            return POST;
        }else if(s.equalsIgnoreCase(PUT.name())){
            return PUT;
        }
        return Select;
    }

}
