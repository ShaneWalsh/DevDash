package dev.dash.enums;

public enum UserTypeEnum {
    Admin,
    Configurator,
    DashboardUser;

    public static UserTypeEnum findType(String s){
        if(s.equalsIgnoreCase(Admin.name())){
            return Admin;
        } else if(s.equalsIgnoreCase(Configurator.name())){
            return Configurator;
        } else if(s.equalsIgnoreCase(DashboardUser.name())){
            return DashboardUser;
        }
        return DashboardUser;
    }

}
