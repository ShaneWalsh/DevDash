package dev.dash.security;

public interface Auditable {
    public Long getId();
    public default String getAuditableName(){
        return this.getClass().getSimpleName();
    }
}
