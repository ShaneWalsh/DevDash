package dev.dash.security;

public interface Auditable {
    public Long getId();
    default String getAuditableName(){
        return this.getClass().getSimpleName();
    }
}
