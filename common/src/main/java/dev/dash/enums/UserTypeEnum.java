package dev.dash.enums;

public enum UserTypeEnum {
    Admin("DD_Admin_Type","ROLE_DD_Admin_Type"),
    Configurator("DD_Configurator_Type","ROLE_DD_Configurator_Type"),
    DashboardUser("DD_DashboardUser_Type","ROLE_DD_DashboardUser_Type");

    String userTypeRole;
    String userTypeRoleWithPrefix;

    UserTypeEnum( String userTypeRole, String userTypeRoleWithPrefix ) {
        this.userTypeRole = userTypeRole;
        this.userTypeRoleWithPrefix = userTypeRoleWithPrefix;
    }

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

    public String getRole(){
        return userTypeRole;
    }   
    
    public String getRoleWithPrefix(){
        return userTypeRoleWithPrefix;
    }

    /**
     * Ensures that the authority is not maskerading as role to enhance permissions
     */
    public static boolean isNotARole(String authority) {
        return !Admin.getRoleWithPrefix().equalsIgnoreCase(authority) && 
               !Configurator.getRoleWithPrefix().equalsIgnoreCase(authority) && 
               !DashboardUser.getRoleWithPrefix().equalsIgnoreCase(authority);
    }
}
