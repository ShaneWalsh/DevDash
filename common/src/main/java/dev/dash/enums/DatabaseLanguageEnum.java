package dev.dash.enums;

public enum DatabaseLanguageEnum {
    MySQL,
    SQLServer,
    CASSANDRA;

    public static DatabaseLanguageEnum findType(String s){
        if(s.equalsIgnoreCase(MySQL.name())){
            return MySQL;
        } else if(s.equalsIgnoreCase(SQLServer.name())){
            return SQLServer;
        } else if(s.equalsIgnoreCase(CASSANDRA.name())){
            return CASSANDRA;
        } 
        return MySQL;
    }
}