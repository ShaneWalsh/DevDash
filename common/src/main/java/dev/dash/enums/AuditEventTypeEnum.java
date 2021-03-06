package dev.dash.enums;

public enum AuditEventTypeEnum {
    EntitySelect,
    EntityInsert,
    EntityUpdate,
    EntityDelete,

    ExecuteQuery,
    ExecuteQueryUserLackingRole, // user is missing the role
    ExecuteQueryFailed, // query threw an error

    GetDashboard,
    GetDashboardUserLackingRole,

    LoginSuccess,
    LoginFailed,

    ExportConfig,
    ImportConfig
    
}
