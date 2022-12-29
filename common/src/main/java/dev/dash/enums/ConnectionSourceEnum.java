package dev.dash.enums;

public enum ConnectionSourceEnum {
    REST,
    MySQL,
    CASSANDRA;

    public static ConnectionSourceEnum findType(String s){
        if(s.equalsIgnoreCase(MySQL.name())){
            return MySQL;
        } else if(s.equalsIgnoreCase(CASSANDRA.name())){
            return CASSANDRA;
        } else if(s.equalsIgnoreCase(REST.name())){
            return REST;
        } 
        return MySQL;
    }
}
