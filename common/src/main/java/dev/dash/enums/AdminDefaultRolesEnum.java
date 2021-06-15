package dev.dash.enums;

public enum AdminDefaultRolesEnum {
    DD_CONFIGURATOR_DASHBOARD("DD_Configurator_Dashboard"),
    DD_CONFIGURATOR_QUERY("DD_Configurator_Query"),
    DD_CONFIGURATOR_PARENT("DD_Configurator_Parent");

    private String securityRoleCode;

    private AdminDefaultRolesEnum(String securityRoleCode) {
        this.securityRoleCode = securityRoleCode;
    }

    public String getSecurityRoleCode() {
        return securityRoleCode;
    }
}
