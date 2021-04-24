package dev.dash.security;

import dev.dash.enums.AuditEventTypeEnum;

public interface AuditLogicService {
    
    public static final String CORRELATION_ID = "correlation-id";

    /**
     * Create an Audit entry for an entity CRUDE event
     * @param auditable the entity
     * @param auditEntityChange the change
     * @param jsonData the data for the event
     */
    public void auditEntityEvent(Auditable auditable, AuditEventTypeEnum auditEntityChange, String jsonData);

}
