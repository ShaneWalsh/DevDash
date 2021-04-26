package dev.dash.security;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import dev.dash.dao.AuditLogRepository;
import dev.dash.enums.AuditEventTypeEnum;
import dev.dash.model.AuditLog;
import dev.dash.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuditLogicServiceImpl  implements AuditLogicService {
    
    @Autowired
    AuditLogRepository auditLogRepository;

    @Override
    public void auditEntityEvent(Auditable auditable, AuditEventTypeEnum auditEntityChange) {    
        auditEntityEvent( auditable, auditEntityChange, JsonUtil.toJSON(auditable) );
    }

    @Override
    public void auditEntityEvent(Object pojo, AuditEventTypeEnum auditEntityChange) {
        auditEntityEvent( new NonAuditable(), auditEntityChange, JsonUtil.toJSON(pojo) );
    }

    @Override
    public void auditEntityEvent(Auditable auditable, AuditEventTypeEnum auditEntityChange, Object pojo) {
        auditEntityEvent( auditable, auditEntityChange, JsonUtil.toJSON(pojo) );
    }

    @Override
    public void auditEntityEvent(Auditable auditable, AuditEventTypeEnum auditEntityChange, String jsonData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if( authentication != null && authentication.isAuthenticated() ) {
            String username = "Unauthenticated";
            Object principle = authentication.getPrincipal();
            if ( principle instanceof UserDetails ) {
                username = ((UserDetails) principle).getUsername();
            } else if ( principle instanceof String ) {
                username = principle.toString();
            } else {
                throw new IllegalStateException("Unknow principle " + principle.getClass().getName());
            }
            insertAudit( auditEntityChange.name(), username, jsonData, auditable.getAuditableName(), auditable.getId() );
        } else {
            log.warn("Unauthenticated entity change {} {}", auditable.getAuditableName(), auditable.getId());
            insertAudit( auditEntityChange.name(), "Unauthenticated", jsonData, auditable.getAuditableName(), auditable.getId() );
        }
    }

    private void insertAudit(String eventType, String securityUser, String dataJson, String objectType, Long objectId){
        // todo get the UUID from the session, x-correlation-id in the header, add filter to extract it and put it in the MDC
        String corIdStr = MDC.get(AuditLogicService.CORRELATION_ID);
        UUID corId = (corIdStr != null) ? UUID.fromString(corIdStr) : UUID.randomUUID();

        log.info("Audit Event:{} User:{} Object:{} Id:{}", eventType, securityUser, objectType, objectId);
        AuditLog auditLog = new AuditLog( corId, eventType, new Timestamp((new Date()).getTime()), 
            securityUser, objectType, objectId, dataJson);
        auditLogRepository.saveAndFlush( auditLog );
    }

    
}

class NonAuditable implements Auditable {

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getAuditableName() {
        return null;
    }

}