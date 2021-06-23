package dev.dash.security;

import dev.dash.enums.AuditEventTypeEnum;

public interface AuditLogicService {
    
    public static final String CORRELATION_ID = "correlation-id";


    /**
     * Create an Audit entry for an entity CRUDE event
     * @param auditable the entity will be converted to JSON
     * @param auditEntityChange the change
     * @param jsonData the data for the event
     */
    public void auditEntityEvent( Auditable auditable, AuditEventTypeEnum auditEntityChange );

   /**
     * Create an Audit entry for an event and a Pojo which will be converted to JSON
     * @param pojo the data for the event
     * @param auditEntityChange the change
     */
    public void auditEntityEvent( Object pojo, AuditEventTypeEnum auditEntityChange);


    /**
     * Create an Audit entry for an entity CRUDE event
     * @param auditable the entity
     * @param auditEntityChange the change
     * @param jsonData the data for the event
     */
    public void auditEntityEvent( Auditable auditable, AuditEventTypeEnum auditEntityChange, Object pojo);

    /**
     * Create an Audit entry for an entity CRUDE event
     * @param auditable the entity
     * @param auditEntityChange the change
     * @param jsonData the data for the event
     */
    public void auditEntityEvent( Auditable auditable, AuditEventTypeEnum auditEntityChange, String jsonData );

}
